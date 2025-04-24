package org.example.hrsservice.services;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.dtos.requests.CdrWithMetadataDTO;
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

    public TarifficationBillDTO chargeCdr(CdrWithMetadataDTO cdrWithMetadataDTO) {
        JsonNode metadata = cdrWithMetadataDTO.metadata();
        systemDatetimeService.setSystemDatetime(cdrWithMetadataDTO.finishDateTime());
        //TODO queues for finished tarification periods bills
        SubscriberTariff subscriberTariff =subscriberTariffRepository.findBySubscriberId(cdrWithMetadataDTO.subscriberId()).orElseThrow(RuntimeException::new);

        log.info(subscriberTariff.toString());

        Tariff tariff = subscriberTariff.getTariff();
        List<TariffPackage> tariffPackages = tariffPackageRepository.findAllByTariff_IdAndServicePackageServiceType(tariff.getId(), ServiceType.MINUTES);
        tariffPackages.sort(Comparator.comparing(TariffPackage::getPriority));
        List<SubscriberPackageUsage> subscriberPackageUsages = subscriberPackageUsageRepository.findAllBySubscriberIdAndIsDeletedFalse(cdrWithMetadataDTO.subscriberId());

        for (TariffPackage tariffPackage: tariffPackages){
            List<PackageRule> rules = packageRuleRepository.findAllByServicePackage_Id(tariffPackage.getServicePackage().getId());
            //check if we have limit, satisfying conditions
            //if we have so we should find 1 rate satisfying conditions
            //we always have 1 rate and at more 1 limit(if no limit found this is pominutnii)(for free calls in package we use rate with value:0)
            PackageRule limitRule = findRuleThatMatchesConditionAndType(rules, cdrWithMetadataDTO, RuleType.LIMIT);
            if (limitRule==null){//that is pominutnii
                PackageRule rateRule = findRuleThatMatchesConditionAndType(rules, cdrWithMetadataDTO, RuleType.RATE);
                BigDecimal price= calculateCallPriceAccordingToRule(rateRule,metadata.get("durationInMinutes").asInt());
                return new TarifficationBillDTO(price,"y.e.", cdrWithMetadataDTO.subscriberId());// this the result return it
            }else {
                //here we should check whether it can be putted in one limit
                //if no we should divide it and tarificate by parts

                PackageRule rateRule = findRuleThatMatchesConditionAndType(rules, cdrWithMetadataDTO,RuleType.RATE);
                BigDecimal price= calculateCallPriceAccordingToRule(rateRule,metadata.get("durationInMinutes").asInt());
                return new TarifficationBillDTO(price,"y.e.", cdrWithMetadataDTO.subscriberId());
                //
            }
        }
        return null;
    }

    private PackageRule findRuleThatMatchesConditionAndType(List<PackageRule> rules, CdrWithMetadataDTO cdrWithMetadataDTO, RuleType ruleType) {
        for (PackageRule rule: rules){
            if (rule.getRuleType().equals(ruleType)){
                if (cdrMatchesCondition(cdrWithMetadataDTO,rule.getCondition())){
                    return rule;
                }
            }
        }
        return null;
    }

    private boolean cdrMatchesCondition(CdrWithMetadataDTO cdrWithMetadataDTO, ConditionNode condition) {
        String conditionType = condition.getType();
        if ("always_true".equals(conditionType)) return true;
        else if ("field".equals(conditionType)) return ifCdrMatchesFieldCondition(cdrWithMetadataDTO,condition);
        else if ("and".equals(conditionType)) {
            List<Boolean> results = new ArrayList<>();
            condition.getConditions().forEach(subCondition -> results.add(cdrMatchesCondition(cdrWithMetadataDTO,subCondition)));
            return !results.contains(false);
        } else if ("or".equals(conditionType)) {
            List<Boolean> results = new ArrayList<>();
            condition.getConditions().forEach(subCondition -> results.add(cdrMatchesCondition(cdrWithMetadataDTO,subCondition)));
            return results.contains(true);
        }

        return false;
    }

    private boolean ifCdrMatchesFieldCondition(CdrWithMetadataDTO cdrWithMetadataDTO, ConditionNode condition) {
        String fieldName = condition.getField();
        String operator = condition.getOperator();
        String conditionValue = condition.getValue();
        if (cdrWithMetadataDTO.metadata().has(fieldName)){
            return compareMetadataValueWithConditionValueViaOperator(cdrWithMetadataDTO.metadata().get(fieldName).asText(), operator, conditionValue);
        } //TODO Reflection or JSONNODE
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
