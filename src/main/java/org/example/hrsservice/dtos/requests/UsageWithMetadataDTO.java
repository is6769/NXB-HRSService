package org.example.hrsservice.dtos.requests;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

@Builder
public record UsageWithMetadataDTO(
        Long subscriberId,
        JsonNode metadata
) {
    public UsageWithMetadataDTO deepClone() {
        return new UsageWithMetadataDTO(
                this.subscriberId,
                this.metadata.deepCopy()
        );
    }
}
