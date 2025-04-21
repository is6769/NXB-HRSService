package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
}
