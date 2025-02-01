package com.example.gympt.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.props.social")
public class SocialProps {
    private SocialInfo kakao;
    private SocialInfo naver;
    private SocialInfo google;

    @Data
    public static class SocialInfo {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String userInfoNameAttributeKey;
        private String clientName;
    }

}
