package org.example.hrsservice.services;

import org.example.hrsservice.entities.SystemDatetime;
import org.example.hrsservice.repositories.SystemDatetimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemDatetimeServiceTest {

    @Mock
    private SystemDatetimeRepository systemDatetimeRepository;

    @InjectMocks
    private SystemDatetimeService systemDatetimeService;
    
    private LocalDateTime fixedTime;
    private SystemDatetime systemDatetime;
    
    @BeforeEach
    void setUp() {
        fixedTime = LocalDateTime.of(2024, 3, 15, 10, 0);
        systemDatetime = new SystemDatetime();
        systemDatetime.setId(1L);
        systemDatetime.setSystemDatetime(fixedTime);
    }

    @Test
    void tryToInitializeSystemDatetime_whenNotExists_createsNewDatetime() {
        when(systemDatetimeRepository.findById(1L)).thenReturn(Optional.empty());
        when(systemDatetimeRepository.save(any(SystemDatetime.class))).thenAnswer(inv -> inv.getArgument(0));
        
        
        systemDatetimeService.tryToInitializeSystemDatetime();
        
        
        ArgumentCaptor<SystemDatetime> captor = ArgumentCaptor.forClass(SystemDatetime.class);
        verify(systemDatetimeRepository).save(captor.capture());
        
        SystemDatetime saved = captor.getValue();
        assertEquals(1L, saved.getId());
        assertNotNull(saved.getSystemDatetime());
        assertTrue(saved.getSystemDatetime().isBefore(LocalDateTime.now().minusMonths(11)));
    }
    
    @Test
    void tryToInitializeSystemDatetime_whenAlreadyExists_doesNotCreateNew() {
        when(systemDatetimeRepository.findById(1L)).thenReturn(Optional.of(systemDatetime));

        systemDatetimeService.tryToInitializeSystemDatetime();

        verify(systemDatetimeRepository, never()).save(any(SystemDatetime.class));
    }
    
    @Test
    void getSystemDatetime_returnsDatetimeFromRepository() {
        when(systemDatetimeRepository.findAll()).thenReturn(List.of(systemDatetime));

        LocalDateTime result = systemDatetimeService.getSystemDatetime();

        assertEquals(fixedTime, result);
    }
    
    @Test
    void setSystemDatetime_withNewerTime_updatesTime() {
        LocalDateTime newTime = fixedTime.plusHours(1);
        when(systemDatetimeRepository.findAll()).thenReturn(List.of(systemDatetime));
        
        
        systemDatetimeService.setSystemDatetime(newTime);
        
        
        assertEquals(newTime, systemDatetime.getSystemDatetime());
        verify(systemDatetimeRepository).save(systemDatetime);
    }
    
    @Test
    void setSystemDatetime_withSameTime_doesNotUpdate() {
        when(systemDatetimeRepository.findAll()).thenReturn(List.of(systemDatetime));
        
        systemDatetimeService.setSystemDatetime(fixedTime);

        verify(systemDatetimeRepository, never()).save(any());
    }
    
    @Test
    void setSystemDatetime_withOlderTime_doesNotUpdate() {
        LocalDateTime olderTime = fixedTime.minusHours(1);
        when(systemDatetimeRepository.findAll()).thenReturn(List.of(systemDatetime));
        
        systemDatetimeService.setSystemDatetime(olderTime);

        assertEquals(fixedTime, systemDatetime.getSystemDatetime());
        verify(systemDatetimeRepository, never()).save(any());
    }
}
