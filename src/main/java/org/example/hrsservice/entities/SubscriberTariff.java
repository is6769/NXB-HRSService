package org.example.hrsservice.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId;


    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    private LocalDateTime cycleStart;

    private LocalDateTime cycleEnd;


}
