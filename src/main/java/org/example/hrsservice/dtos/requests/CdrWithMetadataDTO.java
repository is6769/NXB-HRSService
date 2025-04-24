package org.example.hrsservice.dtos.requests;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;


import java.time.LocalDateTime;

@Builder
public record CdrWithMetadataDTO(
        Long id,
        String callType,
        String servicedMsisdn,
        String otherMsisdn,
        LocalDateTime startDateTime,
        LocalDateTime finishDateTime,
        Long subscriberId,
        JsonNode metadata
) {
    public CdrWithMetadataDTO deepClone() {
        return new CdrWithMetadataDTO(
                this.id,
                this.callType,
                this.servicedMsisdn,
                this.otherMsisdn,
                this.startDateTime,
                this.finishDateTime,
                this.subscriberId,
                this.metadata.deepCopy()
        );
    }
}
