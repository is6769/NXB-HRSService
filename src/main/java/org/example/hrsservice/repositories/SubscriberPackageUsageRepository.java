package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.SubscriberPackageUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriberPackageUsageRepository extends JpaRepository<SubscriberPackageUsage, Long> {
    List<SubscriberPackageUsage> findAllBySubscriberId(Long subscriberId);

    List<SubscriberPackageUsage> findAllBySubscriberIdAndIsDeletedFalse(Long subscriberId);
}
