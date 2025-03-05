package com.example.gympt.domain.member.repository.querydsl;

import com.example.gympt.domain.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMemberTrainerInLocal(Long localId);

}
