package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.SubscriberTariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SubscriberTariffRepository extends JpaRepository<SubscriberTariff, Long> {

    Optional<SubscriberTariff> findBySubscriberId(Long subscriberId);

    List<SubscriberTariff> findAllByCycleEndAfter(LocalDateTime cycleEndAfter);

    List<SubscriberTariff> findAllByCycleEndBefore(LocalDateTime systemDatetime);

    List<SubscriberTariff> findAllByCycleEndBeforeAndTariff_CycleSizeNot(LocalDateTime cycleEndBefore, String tariffCycleSize);
}
