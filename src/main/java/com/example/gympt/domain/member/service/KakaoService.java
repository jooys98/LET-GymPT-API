package com.example.gympt.domain.member.service;

import com.example.gympt.security.MemberAuthDTO;

public interface KakaoService {
    String getKakaoAccessToken(String code);

    MemberAuthDTO getKakaoMember(String accessToken);
}
