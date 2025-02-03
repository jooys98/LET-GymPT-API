package com.example.gympt.domain.member.service;

import com.example.gympt.domain.member.dto.KakaoMoblieRequestDTO;
import com.example.gympt.security.MemberAuthDTO;
import jakarta.validation.Valid;

public interface KakaoService {
    String getKakaoAccessToken(String code);

    MemberAuthDTO getKakaoMember(String accessToken);

    MemberAuthDTO getKakaoMoblieMember(@Valid KakaoMoblieRequestDTO loginRequestDTO);
}
