package org.example.hrsservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая системное время в приложении.
 */
@Entity
@Table(name = "system_datetime")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemDatetime {

    /**
     * Уникальный идентификатор записи системного времени (обычно 1L).
     */
    @Id
    private Long id;

    /**
     * Значение системного времени.
     */
    @Column(name = "system_datetime")
    private LocalDateTime systemDatetime;
}
