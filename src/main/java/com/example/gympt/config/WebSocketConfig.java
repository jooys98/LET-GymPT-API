package com.example.gympt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.net.http.WebSocket;
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        //클라이언트가 서버로 메세지를 보낼떄 /app으로 시작하는 주소를 사용해야 함
        //@MessageMapping 어노텐션과 매핑됨
        registry.enableSimpleBroker("/topic", "/queue");
        //topic : 일대다 메세지 (브로드캐스트 메세지 구현) , queue : 일대일 다른 메세지
        registry.setUserDestinationPrefix("/user");  // 사용자별 메시징을 위한 prefix
    }
}
