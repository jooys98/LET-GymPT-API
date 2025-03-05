package com.example.gympt.domain.category.dto;

import com.example.gympt.domain.gym.enums.Popular;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
//지역별 헬스장을 리스트로 보여주기 위한 dto
public class LocalResponseDTO {
    private Long id;
    private String localName;
    private String gymName;
    private String address;
    private Long dailyPrice;
    private Long monthlyPrice;
    private int likesCount;
    private Popular popular;
   private String gymImage;
}
