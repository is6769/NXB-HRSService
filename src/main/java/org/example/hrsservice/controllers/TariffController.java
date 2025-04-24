package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.requests.CdrWithMetadataDTO;
import org.example.hrsservice.dtos.responses.TarifficationBillDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.web.bind.annotation.*;

@RestController
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping("tariff")
    public TarifficationBillDTO chargeCdr(@RequestBody CdrWithMetadataDTO cdrWithMetadataDTO){
        return tariffService.chargeCdr(cdrWithMetadataDTO);
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
