package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.SubscriberTariff;
import org.example.hrsservice.entities.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriberTariffRepository extends JpaRepository<SubscriberTariff, Long> {

    Optional<SubscriberTariff> findBySubscriberId(Long subscriberId);
}
