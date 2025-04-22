package org.example.hrsservice.entities;

import jakarta.persistence.*;
import lombok.*;

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
