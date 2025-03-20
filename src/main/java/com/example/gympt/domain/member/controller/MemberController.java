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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 관련 API")
public class MemberController {

    private final MemberService memberService;
    private final JWTProps jwtProps;
    private final TrainerService trainerService;

    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 이메일 중복 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 중복 확인 완료",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/check/{email}")
    public ResponseEntity<String> checkEmail(
            @Parameter(description = "확인할 이메일 주소", required = true)
            @PathVariable String email) {
        Boolean isEmailDuplication = memberService.checkedEmail(email);
        return ResponseEntity.ok(isEmailDuplication ? "이미 존재하는 이메일입니다" : "사용가능한 이메일 입니다");
    }



    @Operation(summary = "회원가입", description = "새로운 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 회원"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/join")
    public ResponseEntity<String> join(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "회원가입 요청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = JoinRequestDTO.class)))
            @Valid @RequestBody JoinRequestDTO request) {
        log.info("join: {}", request);
        memberService.join(request);
        return ResponseEntity.ok("회원가입 완료되었습니다");
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 요청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDTO.class)))
            @Valid @RequestBody LoginRequestDTO loginDTO, HttpServletResponse response) {
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

    @Operation(summary = "로그아웃", description = "현재 로그인된 사용자를 로그아웃 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("logout");
        // accessToken은 react 내 redux 상태 지워서 없앰
        // 쿠키 삭제
        CookieUtil.removeTokenCookie(response, "refreshToken");

        return ResponseEntity.ok("logout success!");
    }

    @Operation(summary = "트레이너 신청", description = "일반 회원이 트레이너 신청을 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "트레이너 신청 성공",
                    content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/apply")
    public ResponseEntity<String> trainerApply(
            @Parameter(description = "인증된 회원 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "트레이너 신청 정보", required = true,
                    content = @Content(schema = @Schema(implementation = TrainerSaveRequestDTO.class)))
            @RequestBody TrainerSaveRequestDTO trainerSaveRequestDTO) {
        trainerService.saveTrainer(memberAuthDTO.getEmail(), trainerSaveRequestDTO);
        return ResponseEntity.ok().body("트레이너 신청이 완료 되었습니댜!");
    }

    @Operation(summary = "회원 정보 수정", description = "로그인한 회원의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping
    public ResponseEntity<MemberResponseDTO> modifyMember(
            @Parameter(description = "인증된 회원 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정할 회원 정보", required = true,
                    content = @Content(schema = @Schema(implementation = MemberRequestDTO.class)))
            @RequestBody MemberRequestDTO memberRequestDTO) {
        return ResponseEntity.ok(memberService.updateMember(memberAuthDTO.getEmail(), memberRequestDTO));
    }

    @Operation(summary = "회원 정보 조회", description = "로그인한 회원의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = MemberResponseDTO.class))),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<MemberResponseDTO> getMembers(
            @Parameter(description = "인증된 회원 정보", required = true)
            @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {
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