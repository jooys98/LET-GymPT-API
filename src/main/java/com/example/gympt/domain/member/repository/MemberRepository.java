package com.example.gympt.domain.member.repository;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.querydsl.MemberRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.email = :email")
    Optional<Member> getWithRoles(@Param("email") String email);

    @Query("select m from Member m where m.email = :email")
        // leftjoin + fetch join (한번에 조회 ) Pagination OOM ->
    Optional<Member> findByEmail(@Param("email") String email);

    @Query("select m from Member m where m.email = :email")
    Boolean existsByEmail(String email);

}
