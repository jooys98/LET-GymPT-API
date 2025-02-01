package com.example.gympt.domain.category.repository;

import com.example.gympt.domain.category.entity.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocalRepository extends JpaRepository<Local, Long> {
    @Query("select l from Local l where l.localName = :localName")
    Optional<Local> findByLocalName(@Param("localName") String localName);

//    @Query("select l.id from Local L where l.id = :localId")
//    List<Local> findIdByLocalId(@Param("localId") Long localId);

//    @Query("select l.id from Local l where l.localName = :localName")
//    Optional<Local> findLocalId(@Param("localName") String localName);
}
