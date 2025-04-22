package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    @Query("SELECT t FROM Tariff t WHERE t.id = :id AND t.is_active = true")
    Optional<Tariff> findActiveById(@Param("id") Long id);
}
