package org.example.hrsservice.controllers;

import org.example.hrsservice.services.TariffService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping
    public String tarificateCDR(){
        return "Hello, World";
    }
}
