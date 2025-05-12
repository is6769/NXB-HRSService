package org.example.hrsservice.dtos;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;

/**
 * DTO для передачи информации об использовании услуги вместе с метаданными.
 * Используется для передачи данных о звонках на тарификацию.
 *
 * @param subscriberId Идентификатор абонента.
 * @param metadata Метаданные использования (например, информация о звонке в формате JSON).
 */
@Builder
public record UsageWithMetadataDTO(
        Long subscriberId,
        JsonNode metadata
) {
    /**
     * Создает глубокую копию объекта {@link UsageWithMetadataDTO}.
     * Это необходимо для избежания изменения исходных данных при рекурсивной тарификации.
     * @return Новая глубокая копия объекта.
     */
    public UsageWithMetadataDTO deepClone() {
        return new UsageWithMetadataDTO(
                this.subscriberId,
                this.metadata.deepCopy()
        );
    }
}
