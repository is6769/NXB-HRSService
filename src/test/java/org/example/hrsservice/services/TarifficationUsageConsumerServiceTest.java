package org.example.hrsservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.hrsservice.dtos.TarifficationBillDTO;
import org.example.hrsservice.dtos.UsageWithMetadataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TarifficationUsageConsumerServiceTest {

    @Mock
    private TariffService tariffService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private TarifficationUsageConsumerService consumerService;

    private UsageWithMetadataDTO usageDTO;
    private final Long subscriberId = 1L;
    private final Long callId = 123L;
    private final String exchangeName = "bills.exchange";
    private final String routingKey = "bills.created";

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        ObjectNode metadataNode = mapper.createObjectNode();
        metadataNode.put("callId", callId);
        metadataNode.put("callType", "01");
        metadataNode.put("servicedMsisdn", "79001234567");
        metadataNode.put("otherMsisdn", "79007654321");
        metadataNode.put("startDateTime", LocalDateTime.now().minusMinutes(5).toString());
        metadataNode.put("finishDateTime", LocalDateTime.now().toString());
        metadataNode.put("durationInMinutes", 5);
        metadataNode.put("otherOperator", "Ромашка");
        
        usageDTO = new UsageWithMetadataDTO(subscriberId, metadataNode);

        ReflectionTestUtils.setField(consumerService, "BILLS_EXCHANGE_NAME", exchangeName);
        ReflectionTestUtils.setField(consumerService, "BILLS_ROUTING_KEY", routingKey);
    }

    @Test
    void consumeCallUsageAndSendBill_processesBillCorrectly() {
        BigDecimal amount = new BigDecimal("10.50");
        TarifficationBillDTO bill = new TarifficationBillDTO(amount, "y.e.", subscriberId);
        when(tariffService.chargeCall(any())).thenReturn(bill);

        consumerService.consumeCallUsageAndSendBill(usageDTO);

        verify(tariffService).chargeCall(eq(usageDTO));
        verify(rabbitTemplate).convertAndSend(eq(exchangeName), eq(routingKey), eq(bill));
    }

    @Test
    void consumeCallUsageAndSendBill_withZeroAmount_stillSendsBill() {
        TarifficationBillDTO zeroBill = new TarifficationBillDTO(BigDecimal.ZERO, "y.e.", subscriberId);
        when(tariffService.chargeCall(any())).thenReturn(zeroBill);

        consumerService.consumeCallUsageAndSendBill(usageDTO);

        ArgumentCaptor<TarifficationBillDTO> billCaptor = ArgumentCaptor.forClass(TarifficationBillDTO.class);
        verify(rabbitTemplate).convertAndSend(eq(exchangeName), eq(routingKey), billCaptor.capture());
        
        TarifficationBillDTO capturedBill = billCaptor.getValue();
        assertEquals(BigDecimal.ZERO, capturedBill.amount());
        assertEquals(subscriberId, capturedBill.subscriberId());
    }
}
