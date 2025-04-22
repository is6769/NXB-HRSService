package org.example.hrsservice.services;

import jakarta.transaction.Transactional;
import org.example.hrsservice.dtos.CdrWithMetadataDTO;
import org.example.hrsservice.dtos.TarifficationBillDTO;
import org.example.hrsservice.entities.*;
import org.example.hrsservice.repositories.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TariffService {

    private final TariffRepository tariffRepository;
    private final SubscriberTariffRepository subscriberTariffRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final TariffPackageRepository tariffPackageRepository;
    private final PackageRuleRepository packageRuleRepository;
    private final SubscriberPackageUsageRepository subscriberPackageUsageRepository;

    public TariffService(TariffRepository tariffRepository, SubscriberTariffRepository subscriberTariffRepository, ServicePackageRepository servicePackageRepository, TariffPackageRepository tariffPackageRepository, PackageRuleRepository packageRuleRepository, SubscriberPackageUsageRepository subscriberPackageUsageRepository) {
        this.tariffRepository = tariffRepository;
        this.subscriberTariffRepository = subscriberTariffRepository;
        this.servicePackageRepository = servicePackageRepository;
        this.tariffPackageRepository = tariffPackageRepository;
        this.packageRuleRepository = packageRuleRepository;
        this.subscriberPackageUsageRepository = subscriberPackageUsageRepository;
    }

    public void tarifficateCdr(CdrWithMetadataDTO cdrWithMetadataDTO) {

    }

    @Transactional
    public TarifficationBillDTO setTariffForSubscriber(Long subscriberId, Long tariffId, LocalDateTime currentUnrealDateTime){
        Tariff newTariff = tariffRepository.findActiveById(tariffId).orElseThrow(RuntimeException::new);
        Optional<SubscriberTariff> currentSubscriberTariff = subscriberTariffRepository.findBySubscriberId(subscriberId);
        if (currentSubscriberTariff.isPresent()){
            cleanAllSubscriberInfo(subscriberId);
        }

        SubscriberTariff newSubscriberTariff = SubscriberTariff.builder()
                .tariff(newTariff)
                .subscriberId(subscriberId)
                .cycleStart(currentUnrealDateTime)
                .cycleEnd(currentUnrealDateTime.plusDays(Long.parseLong(newTariff.getCycleSize().split(" ")[0]))) //TODO make more flexible
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
        return new TarifficationBillDTO(calculateTariffPackagesPrice(newTariff),"y.e.");
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
