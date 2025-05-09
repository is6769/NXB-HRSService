package org.example.hrsservice.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.hrsservice.entities.SystemDatetime;
import org.example.hrsservice.repositories.SystemDatetimeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class SystemDatetimeService {

    private final SystemDatetimeRepository systemDatetimeRepository;

    public SystemDatetimeService(SystemDatetimeRepository systemDatetimeRepository) {
        this.systemDatetimeRepository = systemDatetimeRepository;
    }


    @PostConstruct
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void tryToInitializeSystemDatetime(){
        if (systemDatetimeRepository.findById(1L).isEmpty()){
            systemDatetimeRepository.save(SystemDatetime.builder().id(1L).systemDatetime(LocalDateTime.now().minusYears(1)).build());
        }
    }

    public LocalDateTime getSystemDatetime(){
        return systemDatetimeRepository.findAll().get(0).getSystemDatetime();
    }


    public void setSystemDatetime(LocalDateTime newSystemDatetime){
        var systemDatetime = getSystemDatetimeEntity();
        if (newSystemDatetime.isAfter(systemDatetime.getSystemDatetime())){
            systemDatetime.setSystemDatetime(newSystemDatetime);
            systemDatetimeRepository.save(systemDatetime);
        }
    }

    private SystemDatetime getSystemDatetimeEntity(){
        return systemDatetimeRepository.findAll().get(0);
    }
}
