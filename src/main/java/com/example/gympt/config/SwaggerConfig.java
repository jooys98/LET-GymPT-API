package com.example.gympt.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().openapi("3.0.0") // openAPI 버전 명시
                .components(new Components()
                        .addSecuritySchemes("jwt-token",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER).name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("jwt-token"))
                .info(apiInfo());
    }

    private Info apiInfo () {
        return new Info()
                .title("Let Gym-Pt-api Swagger")
                .description("💪Let-gymPT API 는 트레이너와 회원을 연결하는 종합 헬스 플랫폼 API 서비스 입니다. 사용자는 지역별 헬스장과 트레이너를 검색하고 예약할 수 있으며, 역경매 시스템을 통해 자신에게 맞는 조건의 PT를 합리적인 가격에 받을 수 있습니다. WebSocket 기반의 실시간 소통과 AI 기반 리뷰 필터링 및 요약 기능으로 신뢰성 있는 헬스 커뮤니티를 구축합니다.️")
                .version("1.0.0");
    }


}