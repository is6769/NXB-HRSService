package org.example.hrsservice.controllers;

import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestController
public class SubscribersController {

    private final TariffService tariffService;

    public SubscribersController(TariffService tariffService) {
        this.tariffService = tariffService;
    }


    @PutMapping("subscribers/{subscriberId}/tariff/{tariffId}")
    public String setTariffForSubscriber(
            @PathVariable Long subscriberId,
            @PathVariable Long tariffId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime systemDatetime
    )
    {
        if (Objects.nonNull(systemDatetime)){
            log.info(systemDatetime.toString());
            tariffService.setTariffForSubscriber(subscriberId,tariffId,systemDatetime);
        }else {
            tariffService.setTariffForSubscriber(subscriberId,tariffId);
        }
        return "Successfully set tariff.";
    }

    @GetMapping("subscribers/{subscriberId}/tariff")
    public TariffDTO getSubscribersTariffInfo(@PathVariable Long subscriberId){
        return tariffService.getSubscribersTariffInfo(subscriberId);
    }
}
