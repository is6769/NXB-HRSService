package org.example.hrsservice.controllers;

import org.example.hrsservice.dtos.TariffDTO;
import org.example.hrsservice.services.TariffService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TariffsControllerTest {

    @Mock
    private TariffService tariffService;

    @InjectMocks
    private TariffsController tariffsController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tariffsController).build();
    }

    @Test
    void getActiveTariffInfo_shouldReturnTariffInfo() throws Exception {
        Long tariffId = 1L;
        TariffDTO tariffDTO = new TariffDTO(1L, "Premium", "Premium Description", "30 days", true, Collections.emptyList());

        when(tariffService.getActiveTariffInfo(tariffId)).thenReturn(tariffDTO);

        mockMvc.perform(get("/tariffs/{tariffId}", tariffId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Premium")))
                .andExpect(jsonPath("$.description", is("Premium Description")))
                .andExpect(jsonPath("$.cycleSize", is("30 days")))
                .andExpect(jsonPath("$.is_active", is(true)));
    }
}
