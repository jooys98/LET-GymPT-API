package com.example.gympt.domain.category.repository;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.repository.querydsl.LocalRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocalRepository extends JpaRepository<Local, Long> , LocalRepositoryCustom {
    @Query("select l from Local l where l.localName = :localName")
    Optional<Local> findByLocalName(@Param("localName") String localName);


    @Query("select l from Local l where l.id = :localId")
    Optional<Local> findLocalId(@Param("localId") Long localId);


}
