package com.example.gympt.domain.member.service;

import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.security.MemberAuthDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

public interface MemberService {
    void join(@Valid JoinRequestDTO request);

    Map<String, Object> login(String email, String password);

    Map<String, Object> getSocialClaims(MemberAuthDTO memberDTO);

    default MemberAuthDTO toAuthDTO(Member member) {
//회원정보 -> 인증 회원 객체로  변환
        return new MemberAuthDTO(
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getMemberRoleList().stream()
                        .map(Enum::name).toList());
    }


    Boolean checkedEmail(String email);

    Map<String, Object> getSosialClaim(MemberAuthDTO memberAuthDTO);
}