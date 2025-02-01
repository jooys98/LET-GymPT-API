package com.example.gympt.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/health")
public class HealthController {

 private final String uniqueId = UUID.randomUUID().toString();

 @GetMapping
    public String checkHealth() {
     return "server Id" + uniqueId;
 }
} //uniqueId를 통해 여러 서버 인스턴스를 구분
 //서버 생존 확인 , 서버 모니터링 , uuid를 통해 인스턴스 식별

