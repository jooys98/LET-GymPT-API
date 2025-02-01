package com.example.gympt.domain.member.controller;


import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.member.dto.LoginRequestDTO;
import com.example.gympt.domain.member.dto.LoginResponseDTO;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.service.MemberService;
import com.example.gympt.props.JWTProps;
import com.example.gympt.util.CookieUtil;
import com.example.gympt.util.JWTUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final JWTProps jwtProps;


    @GetMapping("/check/{email}")
    public ResponseEntity<String> checkEmail(@PathVariable String email) {
        Boolean isEmailDuplication = memberService.checkedEmail(email);
        return ResponseEntity.ok(isEmailDuplication ? "이미 존재하는 이메일입니다":"사용가능한 이메일 입니다");

    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequestDTO request) {
        log.info("join: {}", request);
        memberService.join(request);
        return ResponseEntity.ok().build();
    }




    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO, HttpServletResponse response) {
        log.info("login: {}", loginDTO);
        Map<String, Object> loginClaims = memberService.login(loginDTO.getEmail(), loginDTO.getPassword());

        // 로그인 성공시 accessToken, refreshToken 생성
        String refreshToken = jwtUtil.generateToken(loginClaims, jwtProps.getRefreshTokenExpirationPeriod());
        String accessToken = loginClaims.get("accessToken").toString();
        // TODO: user 로그인시, refreshToken token 테이블에 저장
//        tokenService.saveRefreshToken(accessToken, refreshToken, memberService.getMember(loginDTO.getEmail()));
        // refreshToken 쿠키로 클라이언트에게 전달
        CookieUtil.setTokenCookie(response, "refreshToken", refreshToken, jwtProps.getRefreshTokenExpirationPeriod()); // 1day

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .name(loginClaims.get("name").toString())
                .roles((List<String>) loginClaims.get("roleNames"))
                .accessToken(accessToken)
                .build();

        log.info("loginResponseDTO: {}", loginResponseDTO);
        // 로그인 성공시, accessToken, email, name, roles 반환
        return ResponseEntity.ok(loginResponseDTO);
    }


    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("logout");
        // accessToken은 react 내 redux 상태 지워서 없앰
        // 쿠키 삭제
        CookieUtil.removeTokenCookie(response, "refreshToken");

        return ResponseEntity.ok("logout success!");
    }

}
