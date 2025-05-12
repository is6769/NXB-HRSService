package org.example.hrsservice.controllers;

import org.example.hrsservice.services.SystemDatetimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты для класса {@link SystemDatetimeController}.
 * Проверяет корректность работы эндпоинта для получения системного времени.
 */
@ExtendWith(MockitoExtension.class)
class SystemDatetimeControllerTest {

    @Mock
    private SystemDatetimeService systemDatetimeService;

    @InjectMocks
    private SystemDatetimeController systemDatetimeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(systemDatetimeController).build();
    }

    /**
     * Тестирует эндпоинт GET /systemDatetime.
     * Ожидается, что будет возвращено системное время, полученное от {@link SystemDatetimeService}.
     * @throws Exception если возникает ошибка при выполнении запроса.
     */
    @Test
    void getSystemDatetime_shouldReturnSystemDatetime() throws Exception {
        LocalDateTime testDateTime = LocalDateTime.of(2023, 1, 1, 12, 0);
        when(systemDatetimeService.getSystemDatetime()).thenReturn(testDateTime);

        mockMvc.perform(get("/systemDatetime"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(2023))
                .andExpect(jsonPath("$[1]").value(1))
                .andExpect(jsonPath("$[2]").value(1))
                .andExpect(jsonPath("$[3]").value(12))
                .andExpect(jsonPath("$[4]").value(0));
    }
}
