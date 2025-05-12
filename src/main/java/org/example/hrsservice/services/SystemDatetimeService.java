package org.example.hrsservice.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.entities.SystemDatetime;
import org.example.hrsservice.repositories.SystemDatetimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Сервис для управления системным временем в приложении.
 * Позволяет инициализировать, получать и устанавливать текущее системное время,
 * которое используется для корректной тарификации и обработки событий.
 */
@Service
@Slf4j
public class SystemDatetimeService {

    private final SystemDatetimeRepository systemDatetimeRepository;

    public SystemDatetimeService(SystemDatetimeRepository systemDatetimeRepository) {
        this.systemDatetimeRepository = systemDatetimeRepository;
    }


    /**
     * Инициализирует системное время при запуске приложения, если оно еще не установлено.
     * Создает запись в базе данных с временем, сдвинутым на год назад от текущего момента.
     * Выполняется в транзакции с уровнем изоляции SERIALIZABLE для предотвращения гонок при инициализации.
     */
    @PostConstruct
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void tryToInitializeSystemDatetime(){
        if (systemDatetimeRepository.findById(1L).isEmpty()){
            systemDatetimeRepository.save(SystemDatetime.builder().id(1L).systemDatetime(LocalDateTime.now().minusYears(1)).build());
        }
    }

    /**
     * Возвращает текущее системное время.
     * @return {@link LocalDateTime}, представляющее текущее системное время.
     */
    public LocalDateTime getSystemDatetime(){
        return systemDatetimeRepository.findAll().get(0).getSystemDatetime();
    }


    /**
     * Устанавливает новое системное время.
     * Обновление происходит только если новое время позже текущего системного времени.
     * @param newSystemDatetime Новое системное время.
     */
    public void setSystemDatetime(LocalDateTime newSystemDatetime){
        var systemDatetime = getSystemDatetimeEntity();
        if (newSystemDatetime.isAfter(systemDatetime.getSystemDatetime())){
            systemDatetime.setSystemDatetime(newSystemDatetime);
            systemDatetimeRepository.save(systemDatetime);
        }
    }

    /**
     * Возвращает сущность {@link SystemDatetime} из базы данных.
     * @return Сущность системного времени.
     */
    private SystemDatetime getSystemDatetimeEntity(){
        return systemDatetimeRepository.findAll().get(0);
    }
}
