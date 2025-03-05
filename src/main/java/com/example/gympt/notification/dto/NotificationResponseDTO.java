package com.example.gympt.notification.dto;

import com.example.gympt.notification.entity.Notification;
import com.example.gympt.notification.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "알림 종류와 목록을 보여주는 dto")
public class NotificationResponseDTO {

    private String trainerImage;
    private String title;
    private String body;
    @Schema(description = "알림 타입 ")
    private NotificationType type;
    @Schema(description = "알림을 받는 사용자의 이메일(로그인 한 회원의 이메일)")
    private String targetEmail;
    @Schema(description = "알림을 받을 권한이 부여되는 fcm 에서 생성한 엑세스 토큰이며 알림 전송에 사용됩니다. ")
    private String fcmToken;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime timestamp;

    public static NotificationResponseDTO from(Notification notification) {
        return NotificationResponseDTO.builder()
                .trainerImage(notification.getMember().getTrainer() == null ? notification.getMember().getProfileImage() : String.valueOf(notification.getMember().getTrainer().getImageList().get(0)))
                .targetEmail(notification.getMember().getEmail())
                .title(notification.getTitle())
                .body(notification.getBody())
                .type(notification.getType())
                .fcmToken(notification.getFcmToken())
                .timestamp(notification.getCreatedAt())
                .build();
    }
}
