package com.example.gympt.domain.likes.dto;

import com.example.gympt.domain.gym.enums.Popular;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class LikesGymDTO {
    //유저의 좋아요 누른 헬스장 조회
    private Long id;
    private String email;
    private String localName;
    private String gymName;
    private String address;
    private String description;
    private Long dailyPrice;
    private Long monthlyPrice;
    private int likesCount;
    private Popular popular;
    private Long gymId;
private String gymImage;
}
