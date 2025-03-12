package com.example.gympt.domain.member.controller;


import com.example.gympt.domain.member.dto.*;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.service.MemberService;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.notification.dto.FcmTokenRequestDTO;
import com.example.gympt.props.JWTProps;
import com.example.gympt.security.MemberAuthDTO;
import com.example.gympt.util.CookieUtil;
import com.example.gympt.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final JWTProps jwtProps;
    private final TrainerService trainerService;

    @GetMapping("/check/{email}")
    public ResponseEntity<String> checkEmail(@PathVariable String email) {
        Boolean isEmailDuplication = memberService.checkedEmail(email);
        return ResponseEntity.ok(isEmailDuplication ? "이미 존재하는 이메일입니다" : "사용가능한 이메일 입니다");

    }

    // 회원가입
    @PostMapping("/join")
    public ResponseEntity<String> join(@Valid @RequestBody JoinRequestDTO request) {
        log.info("join: {}", request);
        memberService.join(request);
        return ResponseEntity.ok("회원가입 완료되었습니다");
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginDTO, HttpServletResponse response) {
        log.info("login: {}", loginDTO);
        Map<String, Object> loginClaims = memberService.login(loginDTO.getEmail(), loginDTO.getPassword());

        // 로그인 성공시 accessToken, refreshToken 생성
        String refreshToken = loginClaims.get("refreshToken").toString();
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

    @PostMapping("/apply")
    //트레이너 신청!!!
    public ResponseEntity<String> trainerApply(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, TrainerSaveRequestDTO trainerSaveRequestDTO) {
        trainerService.saveTrainer(memberAuthDTO.getEmail(), trainerSaveRequestDTO);
        return ResponseEntity.ok().body("트레이너 신청이 완료 되었습니댜!");
    }

    //TODO :/트레이너 정보  수정
//회원정보 수정
    @PutMapping
    public ResponseEntity<MemberResponseDTO> modifyMember(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, MemberRequestDTO memberRequestDTO) {
        return ResponseEntity.ok(memberService.updateMember(memberAuthDTO.getEmail(), memberRequestDTO));
    }

    //자신의 회원 정보 확인
    @GetMapping
    public ResponseEntity<MemberResponseDTO> getMembers(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {
        return ResponseEntity.ok(memberService.getMemberDetail(memberAuthDTO.getEmail()));
    }





    @Operation(summary = "FCM 토큰 업데이트", description = "사용자의 FCM 토큰을 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "FCM 토큰 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/fcm-token")
    public ResponseEntity<Void> updateFCMToken(
            @Parameter(description = "FCM 토큰 업데이트 요청 데이터", required = true)
            @RequestBody FcmTokenRequestDTO request
    ) {
        log.info("updateFCMToken request: {}", request);
        memberService.updateFCMToken(request.getEmail(), request.getFcmToken());
        return ResponseEntity.ok().build();
    }

}
