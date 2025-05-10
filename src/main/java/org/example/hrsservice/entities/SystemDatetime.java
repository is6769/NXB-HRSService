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

@Entity
@Table(name = "system_datetime")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemDatetime {

    @Id
    private Long id;

    @Column(name = "system_datetime")
    private LocalDateTime systemDatetime;
}
