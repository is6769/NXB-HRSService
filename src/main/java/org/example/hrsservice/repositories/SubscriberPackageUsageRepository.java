package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.SubscriberPackageUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberPackageUsageRepository extends JpaRepository<SubscriberPackageUsage, Long> {
}
