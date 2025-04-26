package org.example.hrsservice.controllers;

import org.example.hrsservice.services.SystemDatetimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class SystemDatetimeController {

    private final SystemDatetimeService systemDatetimeService;

    public SystemDatetimeController(SystemDatetimeService systemDatetimeService) {
        this.systemDatetimeService = systemDatetimeService;
    }

    @GetMapping("systemDatetime")
    public LocalDateTime getSystemDatetime(){
        return systemDatetimeService.getSystemDatetime();
    }
}
