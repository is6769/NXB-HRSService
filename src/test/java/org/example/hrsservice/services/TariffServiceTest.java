package org.example.hrsservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.dtos.TarifficationBillDTO;
import org.example.hrsservice.dtos.UsageWithMetadataDTO;
import org.example.hrsservice.entities.*;
import org.example.hrsservice.exceptions.*;
import org.example.hrsservice.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffServiceTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TariffRepository tariffRepository;

    @Mock
    private SubscriberTariffRepository subscriberTariffRepository;

    @Mock
    private TariffPackageRepository tariffPackageRepository;

    @Mock
    private PackageRuleRepository packageRuleRepository;

    @Mock
    private SubscriberPackageUsageRepository subscriberPackageUsageRepository;

    @Mock
    private SystemDatetimeService systemDatetimeService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TariffService tariffService;

    private final LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);
    private final Long subscriberId = 1L;
    private final Long tariffId = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tariffService, "BILLS_ROUTING_KEY", "bills.routing.key");
        ReflectionTestUtils.setField(tariffService, "BILLS_EXCHANGE_NAME", "bills.exchange");
    }

    @Test
    void chargeCall_withInvalidMetadata_shouldThrowException() {
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                null
        );

        assertThrows(InvalidCallMetadataException.class, () -> tariffService.chargeCall(usageWithMetadataDTO));
    }

    @Test
    void chargeCall_withMissingFinishDateTime_shouldThrowException() {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("durationInMinutes", 10);
        
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                metadata
        );

        assertThrows(InvalidCallMetadataException.class, () -> tariffService.chargeCall(usageWithMetadataDTO));
    }

    @Test
    void chargeCall_withMissingDurationInMinutes_shouldThrowException() {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("finishDateTime", "2023-01-01T12:00:00");
        
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                metadata
        );

        assertThrows(InvalidCallMetadataException.class, () -> tariffService.chargeCall(usageWithMetadataDTO));
    }

    @Test
    void chargeCall_withNoSubscriberTariff_shouldThrowException() {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("finishDateTime", "2023-01-01T12:00:00");
        metadata.put("durationInMinutes", 10);
        
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                metadata
        );

        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.empty());

        assertThrows(NoSuchSubscriberTariffException.class, () -> tariffService.chargeCall(usageWithMetadataDTO));
    }

    @Test
    void chargeCall_withInactiveTariff_shouldThrowException() {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("finishDateTime", "2023-01-01T12:00:00");
        metadata.put("durationInMinutes", 10);
        
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                metadata
        );

        Tariff inactiveTariff = createTariff(false);
        SubscriberTariff subscriberTariff = createSubscriberTariff(inactiveTariff);

        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.of(subscriberTariff));

        assertThrows(SubscriberWithInactiveTariffException.class, () -> tariffService.chargeCall(usageWithMetadataDTO));
    }

    @Test
    void chargeCall_withPominutniiTarification_shouldCalculateCorrectly() {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("finishDateTime", "2023-01-01T12:00:00");
        metadata.put("durationInMinutes", 10);
        
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                metadata
        );

        Tariff activeTariff = createTariff(true);
        SubscriberTariff subscriberTariff = createSubscriberTariff(activeTariff);
        TariffPackage tariffPackage = createTariffPackage(activeTariff, 1);
        
        PackageRule rateRule = PackageRule.builder()
                .id(1L)
                .servicePackage(tariffPackage.getServicePackage())
                .ruleType(RuleType.RATE)
                .value(new BigDecimal("2.5"))
                .unit("y.e.")
                .condition(new ConditionNode("always_true",null,null,null,null))
                .build();

        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.of(subscriberTariff));
        when(tariffPackageRepository.findAllByTariff_IdAndServicePackageServiceType(tariffId, ServiceType.MINUTES))
                .thenReturn(new ArrayList<>(List.of(tariffPackage)));
        when(packageRuleRepository.findAllByServicePackage_Id(anyLong())).thenReturn(List.of(rateRule));

        TarifficationBillDTO result = tariffService.chargeCall(usageWithMetadataDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("25.0"), result.amount());
        assertEquals("y.e.", result.unit());
        assertEquals(subscriberId, result.subscriberId());
    }

    @Test
    void chargeCall_withLimitedPackageAndEnoughMinutes_shouldChargeCorrectly() {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("finishDateTime", "2023-01-01T12:00:00");
        metadata.put("durationInMinutes", 10);
        
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                metadata
        );

        Tariff activeTariff = createTariff(true);
        SubscriberTariff subscriberTariff = createSubscriberTariff(activeTariff);
        TariffPackage tariffPackage = createTariffPackage(activeTariff, 1);
        
        PackageRule limitRule = PackageRule.builder()
                .id(1L)
                .servicePackage(tariffPackage.getServicePackage())
                .ruleType(RuleType.LIMIT)
                .value(new BigDecimal("100"))
                .unit("min")
                .condition(new ConditionNode("always_true",null,null,null,null))
                .build();
                
        PackageRule rateRule = PackageRule.builder()
                .id(2L)
                .servicePackage(tariffPackage.getServicePackage())
                .ruleType(RuleType.RATE)
                .value(new BigDecimal("0"))
                .unit("y.e.")
                .condition(new ConditionNode("always_true",null,null,null,null))
                .build();
                
        SubscriberPackageUsage usage = SubscriberPackageUsage.builder()
                .id(1L)
                .subscriberId(subscriberId)
                .servicePackage(tariffPackage.getServicePackage())
                .usedAmount(new BigDecimal("20"))
                .limitAmount(new BigDecimal("100"))
                .isDeleted(false)
                .build();

        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.of(subscriberTariff));
        when(tariffPackageRepository.findAllByTariff_IdAndServicePackageServiceType(tariffId, ServiceType.MINUTES))
                .thenReturn(new ArrayList<>(List.of(tariffPackage)));
        when(packageRuleRepository.findAllByServicePackage_Id(anyLong())).thenReturn(List.of(limitRule, rateRule));
        when(subscriberPackageUsageRepository.findByServicePackageIdAndIsDeletedFalseAndSubscriberId(
                tariffPackage.getServicePackage().getId(), subscriberId)).thenReturn(usage);

        TarifficationBillDTO result = tariffService.chargeCall(usageWithMetadataDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("0"), result.amount());
        assertEquals("y.e.", result.unit());
        assertEquals(subscriberId, result.subscriberId());
        
        ArgumentCaptor<SubscriberPackageUsage> usageCaptor = ArgumentCaptor.forClass(SubscriberPackageUsage.class);
        verify(subscriberPackageUsageRepository).save(usageCaptor.capture());
        assertEquals(new BigDecimal("30"), usageCaptor.getValue().getUsedAmount());
    }

    @Test
    void chargeCall_withLimitedPackageAndPartiallyEnoughMinutes_shouldSplitAndCharge() {
        ObjectNode metadata = objectMapper.createObjectNode();
        metadata.put("finishDateTime", "2023-01-01T12:00:00");
        metadata.put("durationInMinutes", 30);
        
        UsageWithMetadataDTO usageWithMetadataDTO = new UsageWithMetadataDTO(
                subscriberId,
                metadata
        );

        Tariff activeTariff = createTariff(true);
        SubscriberTariff subscriberTariff = createSubscriberTariff(activeTariff);
        TariffPackage tariffPackage = createTariffPackage(activeTariff, 1);
        
        PackageRule limitRule = PackageRule.builder()
                .id(1L)
                .servicePackage(tariffPackage.getServicePackage())
                .ruleType(RuleType.LIMIT)
                .value(new BigDecimal("100"))
                .unit("min")
                .condition(new ConditionNode("always_true",null,null,null,null))
                .build();
                
        PackageRule freeRateRule = PackageRule.builder()
                .id(2L)
                .servicePackage(tariffPackage.getServicePackage())
                .ruleType(RuleType.RATE)
                .value(new BigDecimal("0"))
                .unit("y.e.")
                .condition(new ConditionNode("always_true",null,null,null,null))
                .build();
                
        SubscriberPackageUsage usage = SubscriberPackageUsage.builder()
                .id(1L)
                .subscriberId(subscriberId)
                .servicePackage(tariffPackage.getServicePackage())
                .usedAmount(new BigDecimal("90"))
                .limitAmount(new BigDecimal("100"))
                .isDeleted(false)
                .build();
                
        TariffPackage secondTariffPackage = createTariffPackage(activeTariff, 2);
        PackageRule paidRateRule = PackageRule.builder()
                .id(3L)
                .servicePackage(secondTariffPackage.getServicePackage())
                .ruleType(RuleType.RATE)
                .value(new BigDecimal("2"))
                .unit("y.e.")
                .condition(new ConditionNode("always_true",null,null,null,null))
                .build();

        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.of(subscriberTariff));
        
        when(tariffPackageRepository.findAllByTariff_IdAndServicePackageServiceType(tariffId, ServiceType.MINUTES))
                .thenReturn(Arrays.asList(tariffPackage, secondTariffPackage));
        when(packageRuleRepository.findAllByServicePackage_Id(tariffPackage.getServicePackage().getId()))
                .thenReturn(Arrays.asList(limitRule, freeRateRule));
        when(subscriberPackageUsageRepository.findByServicePackageIdAndIsDeletedFalseAndSubscriberId(
                tariffPackage.getServicePackage().getId(), subscriberId)).thenReturn(usage);
                
        when(packageRuleRepository.findAllByServicePackage_Id(secondTariffPackage.getServicePackage().getId()))
                .thenReturn(List.of(paidRateRule));

        TarifficationBillDTO result = tariffService.chargeCall(usageWithMetadataDTO);

        assertNotNull(result);
        assertEquals(new BigDecimal("40"), result.amount());
        assertEquals("y.e.", result.unit());
        assertEquals(subscriberId, result.subscriberId());
        
        ArgumentCaptor<SubscriberPackageUsage> usageCaptor = ArgumentCaptor.forClass(SubscriberPackageUsage.class);
        verify(subscriberPackageUsageRepository).save(usageCaptor.capture());
        assertEquals(new BigDecimal("100"), usageCaptor.getValue().getUsedAmount());
    }

    @Test
    void chargeExpiredSubscribersTariffs_shouldProcessExpiredTariffs() {
        LocalDateTime systemDatetime = LocalDateTime.of(2023, 1, 10, 0, 0);
        
        Tariff activeTariff = createTariff(true);
        SubscriberTariff expiredSubscriberTariff = SubscriberTariff.builder()
                .id(1L)
                .subscriberId(subscriberId)
                .tariff(activeTariff)
                .cycleStart(LocalDateTime.of(2022, 12, 10, 0, 0))
                .cycleEnd(LocalDateTime.of(2023, 1, 9, 23, 59))
                .build();
        
        when(subscriberTariffRepository.findAllByCycleEndBeforeAndTariff_CycleSizeNot(
                systemDatetime, "0 days")).thenReturn(List.of(expiredSubscriberTariff));
        when(tariffRepository.findActiveById(tariffId)).thenReturn(Optional.of(activeTariff));
        when(tariffPackageRepository.findAllByTariff_Id(tariffId)).thenReturn(new ArrayList<>());

        tariffService.chargeExpiredSubscribersTariffs(systemDatetime);

        verify(tariffRepository).findActiveById(tariffId);
    }

    @Test
    void setTariffForSubscriber_withExistingSubscriberTariff_shouldCleanAndCreate() {
        LocalDateTime systemDatetime = LocalDateTime.of(2023, 1, 1, 12, 0);
        when(systemDatetimeService.getSystemDatetime()).thenReturn(systemDatetime);
        
        Tariff newTariff = Tariff.builder()
                .id(2L)
                .name("New Tariff")
                .description("New Description")
                .cycleSize("30 days")
                .is_active(true)
                .build();
                
        SubscriberTariff currentTariff = createSubscriberTariff(createTariff(true));
        List<SubscriberPackageUsage> currentUsages = List.of(
                SubscriberPackageUsage.builder()
                        .id(1L)
                        .subscriberId(subscriberId)
                        .isDeleted(false)
                        .build()
        );
        
        List<TariffPackage> newTariffPackages = List.of(
                createTariffPackage(newTariff, 1)
        );
        
        List<PackageRule> packageRules = List.of(
                PackageRule.builder()
                        .id(1L)
                        .servicePackage(newTariffPackages.get(0).getServicePackage())
                        .ruleType(RuleType.LIMIT)
                        .value(new BigDecimal("100"))
                        .unit("min")
                        .condition(new ConditionNode("always_true",null,null,null,null))
                        .build(),
                PackageRule.builder()
                        .id(2L)
                        .servicePackage(newTariffPackages.get(0).getServicePackage())
                        .ruleType(RuleType.COST)
                        .value(new BigDecimal("500"))
                        .unit("y.e.")
                        .condition(new ConditionNode("always_true",null,null,null,null))
                        .build()
        );
        
        when(tariffRepository.findActiveById(2L)).thenReturn(Optional.of(newTariff));
        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.of(currentTariff));
        when(subscriberPackageUsageRepository.findAllBySubscriberIdAndIsDeletedFalse(subscriberId))
                .thenReturn(currentUsages);
        when(tariffPackageRepository.findAllByTariff_Id(2L)).thenReturn(newTariffPackages);
        when(packageRuleRepository.findAllByServicePackage_Id(anyLong())).thenReturn(packageRules);

        tariffService.setTariffForSubscriber(subscriberId, 2L);

        verify(subscriberPackageUsageRepository).findAllBySubscriberIdAndIsDeletedFalse(subscriberId);
        verify(subscriberTariffRepository).delete(currentTariff);
        
        ArgumentCaptor<SubscriberTariff> subscriberTariffCaptor = ArgumentCaptor.forClass(SubscriberTariff.class);
        verify(subscriberTariffRepository).save(subscriberTariffCaptor.capture());
        assertEquals(subscriberId, subscriberTariffCaptor.getValue().getSubscriberId());
        assertEquals(newTariff, subscriberTariffCaptor.getValue().getTariff());
        assertEquals(systemDatetime, subscriberTariffCaptor.getValue().getCycleStart());
        assertEquals(systemDatetime.plusDays(30), subscriberTariffCaptor.getValue().getCycleEnd());
        
        ArgumentCaptor<TarifficationBillDTO> billCaptor = ArgumentCaptor.forClass(TarifficationBillDTO.class);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), billCaptor.capture());
        assertEquals(new BigDecimal("500"), billCaptor.getValue().amount());
    }

    @Test
    void getSubscribersTariffInfo_shouldReturnTariffInfo() {
        Tariff tariff = createTariff(true);
        SubscriberTariff subscriberTariff = createSubscriberTariff(tariff);
        List<TariffPackage> tariffPackages = List.of(createTariffPackage(tariff, 1));
        
        tariff.setTariffPackages(tariffPackages);
        
        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.of(subscriberTariff));

        TariffDTO result = tariffService.getSubscribersTariffInfo(subscriberId);

        assertEquals(tariff.getId(), result.id());
        assertEquals(tariff.getName(), result.name());
        assertEquals(tariff.getDescription(), result.description());
        assertEquals(1, result.tariffPackages().size());
    }

    @Test
    void getSubscribersTariffInfo_withNoTariff_shouldThrowException() {
        when(subscriberTariffRepository.findBySubscriberId(subscriberId)).thenReturn(Optional.empty());

        assertThrows(NoSuchSubscriberTariffException.class, () -> tariffService.getSubscribersTariffInfo(subscriberId));
    }

    @Test
    void getActiveTariffInfo_shouldReturnTariffInfo() {
        Tariff tariff = createTariff(true);
        List<TariffPackage> tariffPackages = List.of(createTariffPackage(tariff, 1));

        tariff.setTariffPackages(tariffPackages);
        
        when(tariffRepository.findActiveById(tariffId)).thenReturn(Optional.of(tariff));

        TariffDTO result = tariffService.getActiveTariffInfo(tariffId);

        assertNotNull(result);
        assertEquals(tariff.getId(), result.id());
        assertEquals(tariff.getName(), result.name());
        assertEquals(tariff.getDescription(), result.description());
        assertEquals(1, result.tariffPackages().size());
    }

    @Test
    void getActiveTariffInfo_withNoActiveTariff_shouldThrowException() {
        when(tariffRepository.findActiveById(tariffId)).thenReturn(Optional.empty());

        assertThrows(NoSuchSubscriberTariffException.class, () -> tariffService.getActiveTariffInfo(tariffId));
    }

    private Tariff createTariff(boolean isActive) {
        return Tariff.builder()
                .id(tariffId)
                .name("Test Tariff")
                .description("Test Description")
                .cycleSize("30 days")
                .is_active(isActive)
                .tariffPackages(new ArrayList<>())
                .build();
    }

    private SubscriberTariff createSubscriberTariff(Tariff tariff) {
        return SubscriberTariff.builder()
                .id(1L)
                .subscriberId(subscriberId)
                .tariff(tariff)
                .cycleStart(testDateTime)
                .cycleEnd(testDateTime.plusDays(30))
                .build();
    }

    private TariffPackage createTariffPackage(Tariff tariff, int priority) {
        ServicePackage servicePackage = ServicePackage.builder()
                .id((long) priority)
                .name("Test Service Package " + priority)
                .packageRules(new ArrayList<>())
                .serviceType(ServiceType.MINUTES)
                .build();

        TariffPackage tariffPackage = TariffPackage.builder()
                .id((long) priority)
                .tariff(tariff)
                .servicePackage(servicePackage)
                .priority(priority)
                .build();
        
        if (tariff.getTariffPackages() == null) {
            tariff.setTariffPackages(new ArrayList<>());
        }
        tariff.getTariffPackages().add(tariffPackage);
        
        return tariffPackage;
    }
}
