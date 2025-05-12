package org.example.hrsservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Сущность, представляющая информацию об использовании пакета услуг абонентом.
 * Хранит данные о потребленном количестве услуг и установленном лимите.
 */
@Entity
@Table(name = "subscriber_package_usage")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriberPackageUsage{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор абонента.
     */
    @Column(name = "subscriber_id", nullable = false)
    private Long subscriberId;

    /**
     * Связанный пакет услуг.
     * Использует {@link JsonManagedReference} для управления сериализацией JSON.
     */
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "service_package_id")
    private ServicePackage servicePackage;

    /**
     * Количество использованных единиц услуги.
     */
    private BigDecimal usedAmount;

    /**
     * Лимит пакета услуг.
     */
    private BigDecimal limitAmount;

    /**
     * Единица измерения услуги (например, "minutes", "sms").
     */
    private String unit;

    /**
     * Флаг, указывающий, удалена ли запись об использовании (например, при смене тарифа).
     */
    private Boolean isDeleted;


}
