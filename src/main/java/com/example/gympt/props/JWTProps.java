package com.example.gympt.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("app.props.jwt")
public class JWTProps {
    private String secretKey;
    private int accessTokenExpirationPeriod;
    private int refreshTokenExpirationPeriod;

}
