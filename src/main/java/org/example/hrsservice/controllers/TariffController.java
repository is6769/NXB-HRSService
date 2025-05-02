package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.SubscriberTariffDTO;
import org.example.hrsservice.dtos.requests.UsageWithMetadataDTO;
import org.example.hrsservice.dtos.responses.TarifficationBillDTO;
import org.example.hrsservice.services.TariffService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;
//TODO split to two controllers
@RestController
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @PostMapping("tariff")
    public TarifficationBillDTO chargeCdr(@RequestBody UsageWithMetadataDTO usageWithMetadataDTO){
        return tariffService.chargeCall(usageWithMetadataDTO);
    }

    @PutMapping("subscribers/{subscriberId}/tariff/{tariffId}")
    public String setTariffForSubscriber(
            @PathVariable Long subscriberId,
            @PathVariable Long tariffId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime systemDateTime
    )
    {
        if (Objects.nonNull(systemDateTime)){
            tariffService.setTariffForSubscriber(subscriberId,tariffId,systemDateTime);
        }
        tariffService.setTariffForSubscriber(subscriberId,tariffId);
        return "Successfully set tariff.";
    }

    @GetMapping("subscribers/{subscriberId}")
    public SubscriberTariffDTO getSubscriberTariffInfo(@PathVariable Long subscriberId){
        return tariffService.getSubscriberTariffInfo(subscriberId);
    }
}
