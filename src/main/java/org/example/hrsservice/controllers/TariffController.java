package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.CdrWithMetadataDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
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
    public String setTariffForSubscriber(@PathVariable Long subscriberId, @PathVariable Long tariffId, @RequestParam LocalDateTime currentUnrealDateTime ){
        tariffService.setTariffForSubscriber(subscriberId,tariffId, currentUnrealDateTime);
        return "";
    }
}
