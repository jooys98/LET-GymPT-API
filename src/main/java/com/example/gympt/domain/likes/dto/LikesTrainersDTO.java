package com.example.gympt.domain.likes.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class LikesTrainersDTO {
    private Long id;
    private String email; // 유저의 이메일
    private String name;
    private String gymName;
    private String gender;
    private String local;
    private String trainerImage;
    private int likesCount;


    //그외 나머지 정보는 상세페이지 조회때 보여주기 ~!
}
