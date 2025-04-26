package org.example.hrsservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.dtos.requests.UsageWithMetadataDTO;
import org.example.hrsservice.dtos.responses.TarifficationBillDTO;
import org.example.hrsservice.entities.*;
import org.example.hrsservice.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class TariffService {

    private final TariffRepository tariffRepository;
    private final SubscriberTariffRepository subscriberTariffRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final TariffPackageRepository tariffPackageRepository;
    private final PackageRuleRepository packageRuleRepository;
    private final SubscriberPackageUsageRepository subscriberPackageUsageRepository;
    private final SystemDatetimeService systemDatetimeService;

    public TariffService(TariffRepository tariffRepository, SubscriberTariffRepository subscriberTariffRepository, ServicePackageRepository servicePackageRepository, TariffPackageRepository tariffPackageRepository, PackageRuleRepository packageRuleRepository, SubscriberPackageUsageRepository subscriberPackageUsageRepository, SystemDatetimeService systemDatetimeService) {
        this.tariffRepository = tariffRepository;
        this.subscriberTariffRepository = subscriberTariffRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.tariffPackageRepository = tariffPackageRepository;
        this.packageRuleRepository = packageRuleRepository;
        this.subscriberPackageUsageRepository = subscriberPackageUsageRepository;
        this.systemDatetimeService = systemDatetimeService;
    }

    public TarifficationBillDTO chargeCdr(UsageWithMetadataDTO usageWithMetadataDTO) {
        JsonNode metadata = usageWithMetadataDTO.metadata();
        systemDatetimeService.setSystemDatetime(LocalDateTime.parse(metadata.get("finishDateTime").asText()));
        //TODO queues for finished tarification periods bills
        SubscriberTariff subscriberTariff =subscriberTariffRepository.findBySubscriberId(usageWithMetadataDTO.subscriberId()).orElseThrow(RuntimeException::new);

        log.info(subscriberTariff.toString());

        Tariff tariff = subscriberTariff.getTariff();
        List<TariffPackage> tariffPackages = tariffPackageRepository.findAllByTariff_IdAndServicePackageServiceType(tariff.getId(), ServiceType.MINUTES);
        tariffPackages.sort(Comparator.comparing(TariffPackage::getPriority));

        for (TariffPackage tariffPackage: tariffPackages){
            List<PackageRule> rules = packageRuleRepository.findAllByServicePackage_Id(tariffPackage.getServicePackage().getId());
            //check if we have limit, satisfying conditions
            //if we have so we should find 1 rate satisfying conditions
            //we always have 1 active rate and from 0 to 1 active limit(if no limit found this is pominutnii)(for free calls in package we use rate with value:0)
            PackageRule limitRule = findRuleThatMatchesConditionAndType(rules, usageWithMetadataDTO, RuleType.LIMIT);
            if (limitRule==null){//that is pominutnii
                PackageRule rateRule = findRuleThatMatchesConditionAndType(rules, usageWithMetadataDTO, RuleType.RATE);
                BigDecimal price= calculateCallPriceAccordingToRule(rateRule,metadata.get("durationInMinutes").asInt());
                return new TarifficationBillDTO(price,"y.e.", usageWithMetadataDTO.subscriberId());// this the result return it
            }else {//that is with limit
                //here we should check whether it can be putted in one limit
                //if no we should divide it and tarificate by parts
                SubscriberPackageUsage subscriberPackageUsage = subscriberPackageUsageRepository.findAllByServicePackageIdAndIsDeletedFalse(limitRule.getServicePackage().getId());
                BigDecimal usedAmount = subscriberPackageUsage.getUsedAmount();
                if (usedAmount.compareTo(limitRule.getValue()) < 0){
                    var availableAmount = limitRule.getValue().subtract(usedAmount);
                    var neededAmount = new BigDecimal(usageWithMetadataDTO.metadata().get("durationInMinutes").asInt());
                    if (availableAmount.compareTo(neededAmount) <0 ){
                        List<TarifficationBillDTO> tarifficationBills = new ArrayList<>();

                        var partThatLiesInThisPackage = usageWithMetadataDTO.deepClone();
                        ((ObjectNode)partThatLiesInThisPackage.metadata()).put("durationInMinutes",availableAmount);
                        tarifficationBills.add(chargeCdr(partThatLiesInThisPackage));


                        var amountThatGoesOutOfLimit = neededAmount.subtract(availableAmount);
                        var partThatLiesOutOfThisPackage = usageWithMetadataDTO.deepClone();
                        ((ObjectNode)partThatLiesOutOfThisPackage.metadata()).put("durationInMinutes",amountThatGoesOutOfLimit);
                        tarifficationBills.add(chargeCdr(partThatLiesOutOfThisPackage));

                        return calculateTotalBill(tarifficationBills);
                    }else {//if we have enough minutes to put in package
                        PackageRule rateRule = findRuleThatMatchesConditionAndType(rules, usageWithMetadataDTO,RuleType.RATE);
                        BigDecimal price= calculateCallPriceAccordingToRule(rateRule,metadata.get("durationInMinutes").asInt());
                        subscriberPackageUsage.setUsedAmount(usedAmount.add(neededAmount));
                        subscriberPackageUsageRepository.save(subscriberPackageUsage);

                        return new TarifficationBillDTO(price,"y.e.", usageWithMetadataDTO.subscriberId());
                    }
                }

            }
        }
        return null;
    }

    private TarifficationBillDTO calculateTotalBill(List<TarifficationBillDTO> tarifficationBills) {
        BigDecimal totalPrice = new BigDecimal(0);
        Long subscriberId=tarifficationBills.get(0).subscriberId();

        for (TarifficationBillDTO bill: tarifficationBills){
            totalPrice = totalPrice.add(bill.amount());
        }

        return new TarifficationBillDTO(totalPrice,"y.e.",subscriberId);
    }

    private PackageRule findRuleThatMatchesConditionAndType(List<PackageRule> rules, UsageWithMetadataDTO usageWithMetadataDTO, RuleType ruleType) {
        for (PackageRule rule: rules){
            if (rule.getRuleType().equals(ruleType)){
                if (cdrMatchesCondition(usageWithMetadataDTO,rule.getCondition())){
                    return rule;
                }
            }
        }
        return null;
    }

    private boolean cdrMatchesCondition(UsageWithMetadataDTO usageWithMetadataDTO, ConditionNode condition) {
        String conditionType = condition.getType();
        if ("always_true".equals(conditionType)) return true;
        else if ("field".equals(conditionType)) return ifCdrMatchesFieldCondition(usageWithMetadataDTO,condition);
        else if ("and".equals(conditionType)) {
            List<Boolean> results = new ArrayList<>();
            condition.getConditions().forEach(subCondition -> results.add(cdrMatchesCondition(usageWithMetadataDTO,subCondition)));
            return !results.contains(false);
        } else if ("or".equals(conditionType)) {
            List<Boolean> results = new ArrayList<>();
            condition.getConditions().forEach(subCondition -> results.add(cdrMatchesCondition(usageWithMetadataDTO,subCondition)));
            return results.contains(true);
        }

        return false;
    }

    private boolean ifCdrMatchesFieldCondition(UsageWithMetadataDTO usageWithMetadataDTO, ConditionNode condition) {
        String fieldName = condition.getField();
        String operator = condition.getOperator();
        String conditionValue = condition.getValue();
        if (usageWithMetadataDTO.metadata().has(fieldName)){
            return compareMetadataValueWithConditionValueViaOperator(usageWithMetadataDTO.metadata().get(fieldName).asText(), operator, conditionValue);
        }
        return false;
    }

    private boolean compareMetadataValueWithConditionValueViaOperator(String metadataValue, String operator, String conditionValue) {
        if ("equals".equals(operator)) return metadataValue.equals(conditionValue);
        if ("not_equals".equals(operator)) return !metadataValue.equals(conditionValue);
        return true;
    }

    private BigDecimal calculateCallPriceAccordingToRule(PackageRule rateRule, Integer durationInMinutes) {
        return rateRule.getValue().multiply(new BigDecimal(durationInMinutes));
    }


    @Transactional
    public TarifficationBillDTO setTariffForSubscriber(Long subscriberId, Long tariffId){
        LocalDateTime systemDatetime = systemDatetimeService.getSystemDatetime();
        return setTariffForSubscriber(subscriberId,tariffId, systemDatetime);
    }

    @Transactional
    public TarifficationBillDTO setTariffForSubscriber(Long subscriberId, Long tariffId,LocalDateTime systemDatetime){

        Tariff newTariff = tariffRepository.findActiveById(tariffId).orElseThrow(RuntimeException::new);
        Optional<SubscriberTariff> currentSubscriberTariff = subscriberTariffRepository.findBySubscriberId(subscriberId);
        if (currentSubscriberTariff.isPresent()){
            cleanAllSubscriberInfo(subscriberId);
        }

        SubscriberTariff newSubscriberTariff = SubscriberTariff.builder()
                .tariff(newTariff)
                .subscriberId(subscriberId)
                .cycleStart(systemDatetime)
                .cycleEnd(systemDatetime.plusDays(Long.parseLong(newTariff.getCycleSize().split(" ")[0]))) //TODO make more flexible
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
        return new TarifficationBillDTO(calculateTariffPackagesPrice(newTariff),"y.e.",subscriberId);
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
}
