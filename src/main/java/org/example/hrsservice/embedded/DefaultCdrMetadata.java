package org.example.hrsservice.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DefaultCdrMetadata {
    private Integer durationInMinutes;
    private String otherOperator;
}
