package org.example.hrsservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая связь между абонентом и его тарифом.
 * Хранит информацию о текущем тарифе абонента и периоде его действия.
 */
@Entity
@Table(name = "subscriber_tariff")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberTariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор абонента.
     */
    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId;


    /**
     * Связанный тариф.
     * Использует {@link JsonManagedReference} для управления сериализацией JSON.
     */
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    /**
     * Дата и время начала действия текущего цикла тарифа.
     */
    private LocalDateTime cycleStart;

    /**
     * Дата и время окончания действия текущего цикла тарифа.
     */
    private LocalDateTime cycleEnd;


}
