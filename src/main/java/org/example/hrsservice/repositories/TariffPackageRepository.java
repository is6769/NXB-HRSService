package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.ServicePackage;
import org.example.hrsservice.entities.TariffPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TariffPackageRepository extends JpaRepository<TariffPackage,Long> {
    List<TariffPackage> findAllByTariff_Id(Long tariffId);
}
