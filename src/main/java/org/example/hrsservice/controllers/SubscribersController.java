package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * REST контроллер для управления тарифами абонентов.
 */
@RestController
public class SubscribersController {

    private final TariffService tariffService;

    /**
     * Конструктор контроллера абонентов.
     * @param tariffService Сервис для работы с тарифами.
     */
    public SubscribersController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    /**
     * Обрабатывает PUT-запрос для установки тарифа абоненту.
     * Позволяет опционально указать системное время, на которое устанавливается тариф.
     * @param subscriberId ID абонента.
     * @param tariffId ID тарифа.
     * @param systemDatetime Опциональное системное время (ISO DATE_TIME формат).
     * @return Строка с сообщением об успешной установке тарифа.
     */
    @PutMapping("subscribers/{subscriberId}/tariff/{tariffId}")
    public String setTariffForSubscriber(
            @PathVariable Long subscriberId,
            @PathVariable Long tariffId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime systemDatetime
    )
    {
        if (Objects.nonNull(systemDatetime)){
            tariffService.setTariffForSubscriber(subscriberId,tariffId,systemDatetime);
        }else {
            tariffService.setTariffForSubscriber(subscriberId,tariffId);
        }
        return "Successfully set tariff.";
    }

    /**
     * Обрабатывает GET-запрос для получения информации о текущем тарифе абонента.
     * @param subscriberId ID абонента.
     * @return {@link TariffDTO} с информацией о тарифе абонента.
     */
    @GetMapping("subscribers/{subscriberId}/tariff")
    public TariffDTO getSubscribersTariffInfo(@PathVariable Long subscriberId){
        return tariffService.getSubscribersTariffInfo(subscriberId);
    }
}
