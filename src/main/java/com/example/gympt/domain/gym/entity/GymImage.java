package com.example.gympt.domain.gym.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GymImage {
    private String gymImageName;
    // S3 버킷에 저장될 이미지 이름
    //이 파잉 명이 같을시에 이미지 관련 작업이 이루어진다

    private Integer ord;

    public void setOrd(int ord){
        this.ord = ord;
    }

    public static GymImage convertToGymImage(String imageName){
        return GymImage.builder()
                .gymImageName(imageName)
                .build();
    } // 단일 이미지이름을 파라미터로 받아 GymImage 객체로 바꿔주는 메서드


}
