package com.example.gympt.domain.member.controller;

import com.example.gympt.domain.member.dto.LoginResponseDTO;
import com.example.gympt.domain.member.service.KakaoService;
import com.example.gympt.domain.member.service.MemberService;
import com.example.gympt.props.JWTProps;
import com.example.gympt.security.MemberAuthDTO;
import com.example.gympt.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RequestMapping("/api/kakao/web")
@RestController
@Slf4j
@RequiredArgsConstructor
public class SocialController {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JWTProps jwtProps;

    @GetMapping
    //클라이언트에게서 인가 코드를 받아오고  그걸 카카오 oauth2 에게 전달하여 엑세스 토큰을 받아옴
    public String getKakaoToken(String code) {
        log.info("getKakaoToken" + code);
        return kakaoService.getKakaoAccessToken(code);
    }

    //
    @GetMapping("/login")
    public ResponseEntity<LoginResponseDTO> getKakaoUser(String accessToken, HttpServletResponse response) {
        log.info("getKakaoUser" + accessToken);

        MemberAuthDTO userAuthDTO = kakaoService.getKakaoMember(accessToken);
        //서비스 단에서 받아온 유저의 정보를 토대로 유저 인증 객체 리턴
        Map<String, Object> loginClaim = memberService.getSosialClaim(userAuthDTO);
        String refreshToken = (String) loginClaim.get("refresh_token");
        //유저 인증 객체를 로그인 클레임으로 리턴
        CookieUtil.setTokenCookie(response,"refreshToken", refreshToken ,jwtProps.getRefreshTokenExpirationPeriod());
        //쿠키에 리프레쉬 토큰을 담아 함께 전송

        LoginResponseDTO responseLoginDTO = LoginResponseDTO.builder()
                .email(loginClaim.get("email").toString())
                .roles(Collections.singletonList(loginClaim.get("role").toString()))
                .accessToken((String) loginClaim.get("accessToken"))
                .build();
        log.info("getKakaoUser" + responseLoginDTO);
        return ResponseEntity.ok(responseLoginDTO);
    }

}
