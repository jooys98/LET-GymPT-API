package com.example.gympt.notification.controller;

import com.example.gympt.notification.service.NotificationService;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "로그인 한 사용자의 이메일로 알림 목록 조회합니다")
    @GetMapping("/list")
    public ResponseEntity<?> getNotificationList(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                 @AuthenticationPrincipal MemberAuthDTO memberDTO) {
        log.info("getNotificationList memberDTO : {}", memberDTO);
        return ResponseEntity.ok(notificationService.list(memberDTO.getEmail()));
    }

}
