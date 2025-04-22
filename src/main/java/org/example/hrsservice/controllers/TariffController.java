package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.CdrWithMetadataDTO;
import org.example.hrsservice.dtos.TarifficationBillDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.web.bind.annotation.*;

@RestController
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping("tariff")
    public String chargeCdr(CdrWithMetadataDTO cdrWithMetadataDTO){
        tariffService.chargeCdr(cdrWithMetadataDTO);
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
