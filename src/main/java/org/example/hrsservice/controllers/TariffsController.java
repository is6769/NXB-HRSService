package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST контроллер для получения информации о тарифах.
 */
@RestController
public class TariffsController {

    private final TariffService tariffService;

    public TariffsController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    /**
     * Обрабатывает GET-запрос для получения информации об активном тарифе по его ID.
     * @param tariffId ID тарифа.
     * @return {@link TariffDTO} с информацией об активном тарифе.
     */
    @GetMapping("tariffs/{tariffId}")
    public TariffDTO getActiveTariffInfo(@PathVariable Long tariffId){
        return tariffService.getActiveTariffInfo(tariffId);
    }
}
