package org.example.hrsservice.dtos.requests;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;


import java.time.LocalDateTime;

@Builder
public record CdrWithMetadataDTO(
        Long subscriberId,
        JsonNode metadata
) {
    public CdrWithMetadataDTO deepClone() {
        return new CdrWithMetadataDTO(
                this.subscriberId,
                this.metadata.deepCopy()
        );
    }
}
