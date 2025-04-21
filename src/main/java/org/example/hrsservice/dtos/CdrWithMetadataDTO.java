package org.example.hrsservice.dtos;

import lombok.Builder;
import org.example.hrsservice.embedded.DefaultCdrMetadata;


import java.time.LocalDateTime;

@Builder
public record CdrWithMetadataDTO(
        Long id,
        String callType,
        String servicedMsisdn,
        String otherMsisdn,
        LocalDateTime startDateTime,
        LocalDateTime finishDateTime,
        DefaultCdrMetadata cdrMetadata
) {

}
