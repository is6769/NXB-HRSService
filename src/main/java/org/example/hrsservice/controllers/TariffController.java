package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.CdrWithMetadataDTO;
import org.example.hrsservice.dtos.TarifficationBillDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping("tariff")
    public String tarifficateCdr(CdrWithMetadataDTO cdrWithMetadataDTO){
        tariffService.tarifficateCdr(cdrWithMetadataDTO);
        return "Hello, World";
    }

    @PutMapping("subscribers/{subscriberId}/tariff/{tariffId}")
    public TarifficationBillDTO setTariffForSubscriber(
            @PathVariable Long subscriberId,
            @PathVariable Long tariffId
    )
    {
        return tariffService.setTariffForSubscriber(subscriberId,tariffId);
    }
}
