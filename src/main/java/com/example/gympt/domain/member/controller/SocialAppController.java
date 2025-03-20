package com.example.gympt.domain.member.controller;

import com.example.gympt.domain.member.dto.KakaoMoblieRequestDTO;
import com.example.gympt.domain.member.dto.LoginRequestDTO;
import com.example.gympt.domain.member.dto.LoginResponseDTO;
import com.example.gympt.domain.member.service.KakaoService;
import com.example.gympt.domain.member.service.MemberService;
import com.example.gympt.props.JWTProps;
import com.example.gympt.security.MemberAuthDTO;
import com.example.gympt.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/kakao/mobile")
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Social Login API", description = "모바일 소셜 로그인 관련 API")
public class SocialAppController {

    private final MemberService memberService;
    private final KakaoService kakaoService;
    private final JWTProps jwtProps;

    @Operation(summary = "카카오 모바일 로그인", description = "카카오 소셜 로그인을 통해 모바일에서 인증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> kakaoMoblieLogin(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "카카오 모바일 로그인 요청 정보", required = true,
            content = @Content(schema = @Schema(implementation = KakaoMoblieRequestDTO.class)))
                                                             @Valid @RequestBody KakaoMoblieRequestDTO loginRequestDTO,
                                                             HttpServletResponse response) {
        log.info("kakaoMoblie login request: {}", loginRequestDTO);

        MemberAuthDTO memberAuthDTO = kakaoService.getKakaoMoblieMember(loginRequestDTO);
        Map<String, Object> kakaoClaims = memberService.getSosialClaim(memberAuthDTO);
        String refreshToken = kakaoClaims.get("refresh_token").toString();
        String accessToken = kakaoClaims.get("access_token").toString();
        //인증 클레임으로 만들고 쿠키 + 토큰과 함께 보낸다
        CookieUtil.setTokenCookie(response, "refreshToken", refreshToken, jwtProps.getRefreshTokenExpirationPeriod());
        //쿠키랑 같이 보내기

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(kakaoClaims.get("email").toString())
                .name(kakaoClaims.get("name").toString())
                .roles((List<String>) kakaoClaims.get("roleNames"))
                .accessToken(accessToken)
                .build();

        log.info("Kakao login response: {}", loginResponseDTO);
        return ResponseEntity.ok(loginResponseDTO);

    }
}
