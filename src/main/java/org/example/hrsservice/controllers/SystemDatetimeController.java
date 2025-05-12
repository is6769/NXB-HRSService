package org.example.hrsservice.controllers;

import org.example.hrsservice.services.SystemDatetimeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * REST контроллер для управления системным временем.
 */
@RestController
public class SystemDatetimeController {

    private final SystemDatetimeService systemDatetimeService;

    public SystemDatetimeController(SystemDatetimeService systemDatetimeService) {
        this.systemDatetimeService = systemDatetimeService;
    }

    /**
     * Обрабатывает GET-запрос для получения текущего системного времени.
     * @return {@link LocalDateTime}, представляющее текущее системное время.
     */
    @GetMapping("systemDatetime")
    public LocalDateTime getSystemDatetime(){
        return systemDatetimeService.getSystemDatetime();
    }
}
