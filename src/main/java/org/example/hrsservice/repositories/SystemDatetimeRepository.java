package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.PackageRule;
import org.example.hrsservice.entities.SystemDatetime;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SystemDatetimeRepository extends JpaRepository<SystemDatetime, Long> {

    @Override
    <S extends SystemDatetime> List<S> findAll(Example<S> example);
}
