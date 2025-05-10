package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.PackageRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRuleRepository extends JpaRepository<PackageRule, Long> {
    List<PackageRule> findAllByServicePackage_Id(Long servicePackageId);
}
