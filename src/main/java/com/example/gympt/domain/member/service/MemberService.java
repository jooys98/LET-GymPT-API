package com.example.gympt.domain.member.service;

import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.member.dto.MemberRequestDTO;
import com.example.gympt.domain.member.dto.MemberResponseDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.security.MemberAuthDTO;
import jakarta.validation.Valid;

import java.util.Map;

public interface MemberService {
    void join(@Valid JoinRequestDTO request);

    Map<String, Object> login(String email, String password);


    default MemberAuthDTO toAuthDTO(Member member) {
        return null;
    }

    Boolean checkedEmail(String email);

    Map<String, Object> getSosialClaim(MemberAuthDTO memberAuthDTO);

    MemberResponseDTO updateMember(String email, MemberRequestDTO memberRequestDTO);



    MemberResponseDTO getMemberDetail(String email);

    void updateFCMToken(String email, String fcmToken);
}