package com.example.gympt.domain.gym.dto;

import com.example.gympt.domain.gym.enums.Popular;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class GymSearchRequestDTO {
    //querydsl 을 위한 검색 조건들!!
    private String gymName;
    private String localName;
    private Long dailyPrice;
    private Long monthlyPrice;
    private Popular popular;
    private String searchKeyword;
    //사용자를 위한 다양한 검색 조건!!!
    private Long minMonthlyPrice;//최소 한달 가격
    private Long maxMonthlyPrice; // 최대한달 가격
    private Long minDailyPrice; // 최소 하루 가격
    private Long maxDailyPrice; // 최대 하루 가격


}
