package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TariffsController {

    private final TariffService tariffService;

    public TariffsController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping("tariffs/{tariffId}")
    public TariffDTO getActiveTariffInfo(@PathVariable Long tariffId){
        return tariffService.getActiveTariffInfo(tariffId);
    }
}
