package org.example.hrsservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.dtos.UsageWithMetadataDTO;
import org.example.hrsservice.dtos.TarifficationBillDTO;
import org.example.hrsservice.entities.*;
import org.example.hrsservice.exceptions.*;
import org.example.hrsservice.repositories.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class TariffService {

    @Value("${const.rabbitmq.bills.BILLS_ROUTING_KEY}")
    private String BILLS_ROUTING_KEY;

    @Value("${const.rabbitmq.bills.BILLS_EXCHANGE_NAME}")
    private String BILLS_EXCHANGE_NAME;

    private final TariffRepository tariffRepository;
    private final SubscriberTariffRepository subscriberTariffRepository;
    private final TariffPackageRepository tariffPackageRepository;
    private final PackageRuleRepository packageRuleRepository;
    private final SubscriberPackageUsageRepository subscriberPackageUsageRepository;
    private final SystemDatetimeService systemDatetimeService;
    private final RabbitTemplate rabbitTemplate;

    public TariffService(TariffRepository tariffRepository, SubscriberTariffRepository subscriberTariffRepository, TariffPackageRepository tariffPackageRepository, PackageRuleRepository packageRuleRepository, SubscriberPackageUsageRepository subscriberPackageUsageRepository, SystemDatetimeService systemDatetimeService, RabbitTemplate rabbitTemplate) {
        this.tariffRepository = tariffRepository;
        this.subscriberTariffRepository = subscriberTariffRepository;
        this.tariffPackageRepository = tariffPackageRepository;
        this.packageRuleRepository = packageRuleRepository;
        this.subscriberPackageUsageRepository = subscriberPackageUsageRepository;
        this.systemDatetimeService = systemDatetimeService;
        this.rabbitTemplate = rabbitTemplate;
    }

    public TarifficationBillDTO chargeCall(UsageWithMetadataDTO usageWithMetadataDTO) {
        RuleFinderService ruleFinderService = new RuleFinderService();

        JsonNode metadata = usageWithMetadataDTO.metadata();

        validateMetadata(metadata);

        systemDatetimeService.setSystemDatetime(LocalDateTime.parse(metadata.get("finishDateTime").asText()));
        chargeExpiredSubscribersTariffs(LocalDateTime.parse(metadata.get("finishDateTime").asText()));

        Long subscriberId = usageWithMetadataDTO.subscriberId();

        SubscriberTariff subscriberTariff=subscriberTariffRepository.findBySubscriberId(subscriberId).orElseThrow(()->new NoSuchSubscriberTariffException("The subscriber with id: %d dont have active tariff.".formatted(subscriberId)));
        Tariff tariff = subscriberTariff.getTariff();
        if (!tariff.getIs_active()) throw new SubscriberWithInactiveTariffException("The subscriber with id: %d has inactive tariff with id: %d".formatted(subscriberId,tariff.getId()));

        List<TariffPackage> tariffPackages = tariffPackageRepository.findAllByTariff_IdAndServicePackageServiceType(tariff.getId(), ServiceType.MINUTES);
        tariffPackages.sort(Comparator.comparing(TariffPackage::getPriority));

        for (TariffPackage tariffPackage: tariffPackages){
            List<PackageRule> rules = packageRuleRepository.findAllByServicePackage_Id(tariffPackage.getServicePackage().getId());
            //check if we have limit, satisfying conditions
            //if we have so we should find 1 rate satisfying conditions
            //we always have 1 active rate and from 0 to 1 active limit(if no limit found this is pominutnii)(for free calls in package we use rate with value:0)
            PackageRule limitRule = ruleFinderService.findRuleThatMatchesConditionAndType(rules, usageWithMetadataDTO, RuleType.LIMIT);
            if (limitRule==null){//that is pominutnii
                PackageRule rateRule = ruleFinderService.findRuleThatMatchesConditionAndType(rules, usageWithMetadataDTO, RuleType.RATE);
                BigDecimal price= calculateCallPriceAccordingToRule(rateRule,metadata.get("durationInMinutes").asInt());
                return new TarifficationBillDTO(price,"y.e.", subscriberId);// this the result return it
            }else {//that is with limit
                //here we should check whether it can be putted in one limit
                //if no we should divide it and tarificate by parts
                SubscriberPackageUsage subscriberPackageUsage = subscriberPackageUsageRepository.findByServicePackageIdAndIsDeletedFalseAndSubscriberId(limitRule.getServicePackage().getId(),subscriberId);
                BigDecimal usedAmount = subscriberPackageUsage.getUsedAmount();
                if (usedAmount.compareTo(limitRule.getValue()) < 0){
                    var availableAmount = limitRule.getValue().subtract(usedAmount);
                    var neededAmount = new BigDecimal(usageWithMetadataDTO.metadata().get("durationInMinutes").asInt());
                    if (availableAmount.compareTo(neededAmount) <0 ){
                        List<TarifficationBillDTO> tarifficationBills = new ArrayList<>();

                        var partThatLiesInThisPackage = usageWithMetadataDTO.deepClone();
                        ((ObjectNode)partThatLiesInThisPackage.metadata()).put("durationInMinutes",availableAmount);
                        tarifficationBills.add(chargeCall(partThatLiesInThisPackage));


                        var amountThatGoesOutOfLimit = neededAmount.subtract(availableAmount);
                        var partThatLiesOutOfThisPackage = usageWithMetadataDTO.deepClone();
                        ((ObjectNode)partThatLiesOutOfThisPackage.metadata()).put("durationInMinutes",amountThatGoesOutOfLimit);
                        tarifficationBills.add(chargeCall(partThatLiesOutOfThisPackage));

                        return calculateTotalBill(tarifficationBills);
                    }else {//if we have enough minutes to put in package
                        PackageRule rateRule = ruleFinderService.findRuleThatMatchesConditionAndType(rules, usageWithMetadataDTO,RuleType.RATE);
                        BigDecimal price= calculateCallPriceAccordingToRule(rateRule,metadata.get("durationInMinutes").asInt());
                        subscriberPackageUsage.setUsedAmount(usedAmount.add(neededAmount));
                        subscriberPackageUsageRepository.save(subscriberPackageUsage);

                        return new TarifficationBillDTO(price,"y.e.", subscriberId);
                    }
                }

            }
        }
        throw new CannotChargeCallException("Cannot charge call: %s".formatted(usageWithMetadataDTO.toString()));
    }

    private void validateMetadata(JsonNode metadata) {
        if (!metadata.has("finishDateTime")) throw new InvalidCallMetadataException("finishDateTime");
        if (!metadata.has("durationInMinutes")) throw new InvalidCallMetadataException("durationInMinutes");
    }

    private TarifficationBillDTO calculateTotalBill(List<TarifficationBillDTO> tarifficationBills) {
        BigDecimal totalPrice = new BigDecimal(0);
        Long subscriberId=tarifficationBills.get(0).subscriberId();

        for (TarifficationBillDTO bill: tarifficationBills){
            totalPrice = totalPrice.add(bill.amount());
        }

        return new TarifficationBillDTO(totalPrice,"y.e.",subscriberId);
    }

    private BigDecimal calculateCallPriceAccordingToRule(PackageRule rateRule, Integer durationInMinutes) {
        return rateRule.getValue().multiply(new BigDecimal(durationInMinutes));
    }

    //@Transactional
    public void chargeExpiredSubscribersTariffs(LocalDateTime systemDatetime){
        List<SubscriberTariff> expiredList = subscriberTariffRepository.findAllByCycleEndBeforeAndTariff_CycleSizeNot(systemDatetime,"0 days");
        expiredList.forEach(expired -> {
            log.info("MONTHLY BILLING: {}     systemdatetime: {}", expired, systemDatetime);
            setTariffForSubscriber(expired.getSubscriberId(),expired.getTariff().getId(),systemDatetime);
        });
    }


    @Transactional
    public void setTariffForSubscriber(Long subscriberId, Long tariffId){
        LocalDateTime systemDatetime = systemDatetimeService.getSystemDatetime();
        setTariffForSubscriber(subscriberId,tariffId, systemDatetime);
    }

    @Transactional
    public void setTariffForSubscriber(Long subscriberId, Long tariffId,LocalDateTime systemDatetime){

        Tariff newTariff = tariffRepository.findActiveById(tariffId).orElseThrow(()->new NoSuchTariffException("The tariff with id: %d is not present.".formatted(tariffId)));
        Optional<SubscriberTariff> currentSubscriberTariff = subscriberTariffRepository.findBySubscriberId(subscriberId);
        if (currentSubscriberTariff.isPresent()){
            cleanAllSubscriberInfo(subscriberId);
        }

        SubscriberTariff newSubscriberTariff = SubscriberTariff.builder()
                .tariff(newTariff)
                .subscriberId(subscriberId)
                .cycleStart(systemDatetime)
                .cycleEnd(systemDatetime.plusDays(Long.parseLong(newTariff.getCycleSize().split(" ")[0]))) // postgres intervals
                .build();

        subscriberTariffRepository.save(newSubscriberTariff);

        List<TariffPackage> tariffPackages = tariffPackageRepository.findAllByTariff_Id(tariffId);

        tariffPackages.forEach(tariffPackage -> {
            List<PackageRule> packageRules = packageRuleRepository.findAllByServicePackage_Id(tariffPackage.getServicePackage().getId());
            for (PackageRule packageRule: packageRules){
                if (packageRule.getRuleType().equals(RuleType.LIMIT)) {
                    SubscriberPackageUsage subscriberPackageUsage = SubscriberPackageUsage.builder()
                            .subscriberId(subscriberId)
                            .servicePackage(tariffPackage.getServicePackage())
                            .usedAmount(new BigDecimal(0))
                            .limitAmount(packageRule.getValue())
                            .unit(packageRule.getUnit())
                            .isDeleted(false)
                            .build();
                    subscriberPackageUsageRepository.save(subscriberPackageUsage);
                }
            }
        });
        produceBill(new TarifficationBillDTO(calculateTariffPackagesPrice(newTariff),"y.e.",subscriberId));
    }

    public void produceBill(TarifficationBillDTO bill){
        rabbitTemplate.convertAndSend(BILLS_EXCHANGE_NAME,BILLS_ROUTING_KEY,bill);
    }

    public void cleanAllSubscriberInfo(Long subscriberId){
        List<SubscriberPackageUsage> subscriberPackageUsages = subscriberPackageUsageRepository.findAllBySubscriberIdAndIsDeletedFalse(subscriberId);
        SubscriberTariff subscriberTariff = subscriberTariffRepository.findBySubscriberId(subscriberId).get();
        subscriberPackageUsages.forEach(subscriberPackageUsage -> {
            subscriberPackageUsage.setIsDeleted(true);
            subscriberPackageUsageRepository.save(subscriberPackageUsage);
        });

        subscriberTariffRepository.delete(subscriberTariff);
    }

    private BigDecimal calculateTariffPackagesPrice(Tariff tariff){
        List<TariffPackage> tariffPackages = tariffPackageRepository.findAllByTariff_Id(tariff.getId());
        BigDecimal totalCost = new BigDecimal(0);
        for (TariffPackage tariffPackage: tariffPackages){
            List<PackageRule> packageRules = packageRuleRepository.findAllByServicePackage_Id(tariffPackage.getServicePackage().getId());
            for (PackageRule packageRule: packageRules){
                if (packageRule.getRuleType().equals(RuleType.COST)){
                    totalCost = totalCost.add(packageRule.getValue());
                }
            }
        }
        return totalCost;
    }

    public TariffDTO getSubscribersTariffInfo(Long subscriberId) {
        SubscriberTariff subscriberTariff = subscriberTariffRepository.findBySubscriberId(subscriberId).orElseThrow(()->new NoSuchSubscriberTariffException("This subscriber dont have active tariffs."));
        return TariffDTO.fromEntity(subscriberTariff.getTariff());
    }

    public TariffDTO getActiveTariffInfo(Long tariffId){
        return TariffDTO.fromEntity(tariffRepository.findActiveById(tariffId).orElseThrow(()->new NoSuchSubscriberTariffException("No such active tariff.")));
    }
}
