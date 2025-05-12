package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.services.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для класса {@link SubscribersController}.
 * Проверяет корректность работы эндпоинтов для управления тарифами абонентов.
 */
@ExtendWith(MockitoExtension.class)
class SubscribersControllerTest {

    @Mock
    private TariffService tariffService;

    @InjectMocks
    private SubscribersController subscribersController;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscribersController).build();
    }

    /**
     * Тестирует эндпоинт PUT /subscribers/{subscriberId}/tariff/{tariffId} без указания systemDatetime.
     * Ожидается, что будет вызван соответствующий метод сервиса {@link TariffService} без параметра времени.
     * @throws Exception если возникает ошибка при выполнении запроса.
     */
    @Test
    void setTariffForSubscriber_withoutSystemDatetime_shouldCallServiceWithoutDatetime() throws Exception {
        Long subscriberId = 1L;
        Long tariffId = 2L;

        mockMvc.perform(put("/subscribers/{subscriberId}/tariff/{tariffId}", subscriberId, tariffId))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully set tariff."));

        verify(tariffService).setTariffForSubscriber(subscriberId, tariffId);
    }

    /**
     * Тестирует эндпоинт PUT /subscribers/{subscriberId}/tariff/{tariffId} с указанием systemDatetime.
     * Ожидается, что будет вызван соответствующий метод сервиса {@link TariffService} с параметром времени.
     * @throws Exception если возникает ошибка при выполнении запроса.
     */
    @Test
    void setTariffForSubscriber_withSystemDatetime_shouldCallServiceWithDatetime() throws Exception {
        Long subscriberId = 1L;
        Long tariffId = 2L;
        LocalDateTime systemDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);

        mockMvc.perform(put("/subscribers/{subscriberId}/tariff/{tariffId}", subscriberId, tariffId)
                .param("systemDatetime", "2023-01-01T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(content().string("Successfully set tariff."));

        verify(tariffService).setTariffForSubscriber(subscriberId, tariffId, systemDateTime);
    }

    /**
     * Тестирует эндпоинт GET /subscribers/{subscriberId}/tariff.
     * Ожидается, что будет возвращена информация о тарифе абонента, полученная от {@link TariffService}.
     * @throws Exception если возникает ошибка при выполнении запроса.
     */
    @Test
    void getSubscribersTariffInfo_shouldReturnTariffInfo() throws Exception {
        Long subscriberId = 1L;
        TariffDTO tariffDTO = new TariffDTO(1L, "Basic", "Basic Description", "30 days", true, Collections.emptyList());

        when(tariffService.getSubscribersTariffInfo(subscriberId)).thenReturn(tariffDTO);

        mockMvc.perform(get("/subscribers/{subscriberId}/tariff", subscriberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Basic")))
                .andExpect(jsonPath("$.description", is("Basic Description")))
                .andExpect(jsonPath("$.cycleSize", is("30 days")))
                .andExpect(jsonPath("$.is_active", is(true)));
    }
}
