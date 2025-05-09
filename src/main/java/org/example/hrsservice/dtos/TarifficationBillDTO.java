package org.example.hrsservice.dtos;

import java.math.BigDecimal;

public record TarifficationBillDTO(
        BigDecimal amount,
        String unit,
        Long subscriberId
) {
}
