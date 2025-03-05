package com.example.gympt.notification.service;

import com.example.gympt.notification.enums.NotificationType;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class FCMService {

    public void sendNotification(String targetEmail,String token, String title, String body, NotificationType type) {
        log.info("sendNotification token: {}, title: {}, body: {}, type: {}", token, title, body, type);
        Message message = Message.builder()
                .putData("type", type.name())
                .putData("targetEmail", targetEmail)
                .putData("title", title)  // 데이터에도 추가
                .putData("body", body)    // 데이터에도 추가
                .putData("timestamp", LocalDateTime.now().toString()) // 데이터에도 추가
                .setToken(token)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM Token: {}", token);
            log.info("Message: {}", message);
            log.info("Successfully sent notification: {}", response);
        } catch (Exception e) {
            log.error("Failed to send notification", e);
        }
    }



    // 단체 알림 발송
    public void sendMulticastNotification(List<String> tokenList, String title, String body, NotificationType type) {
        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("type", type.name())
                .putData("title", title)
                .putData("body", body)
                .putData("timestamp", LocalDateTime.now().toString())
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("Multicast 메시지 전송 완료: 성공={}, 실패={}",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티캐스트 메시지 전송 실패", e);
        }
    }
}