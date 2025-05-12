package org.example.hrsservice.repositories;

import org.example.hrsservice.entities.SystemDatetime;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Репозиторий для управления сущностью {@link SystemDatetime}.
 */
public interface SystemDatetimeRepository extends JpaRepository<SystemDatetime, Long> {

    @Override
    <S extends SystemDatetime> List<S> findAll(Example<S> example);
}
