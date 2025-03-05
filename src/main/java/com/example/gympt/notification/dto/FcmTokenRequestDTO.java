package com.example.gympt.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = " FCM(Firebase Cloud Messaging) 토큰을 서버에 전달하기 위한 데이터 전송 객체(DTO) 이며 2가지의 정보를 캡슐화 한다")
public class FcmTokenRequestDTO {
    @Schema(description = "푸시 알림을 보내기 위해 firebase 에서 생성된 고유 토큰")
    private String fcmToken;
    @Schema(description = "어떤 사용자의 FCM 토큰인지 식별하기 위한 사용자 이메일")
    private String email;
}