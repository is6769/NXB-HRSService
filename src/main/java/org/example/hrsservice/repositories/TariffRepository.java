package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    Optional<Tariff> findByIdAndIs_activeIsTrue(Long id);
}
