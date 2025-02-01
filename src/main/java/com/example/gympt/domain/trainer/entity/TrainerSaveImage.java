package com.example.gympt.domain.trainer.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainerSaveImage {

    //트레이너 신청 이미지 리스트
    private String trainerSaveImageName;

    private Integer ord;

    public void setOrd(int ord){
        this.ord = ord;
    }
}
