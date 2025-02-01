package com.example.gympt.domain.trainer.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerImage {
    private String trainerImageName;

    private Integer ord;

    public void setOrd(int ord){
        this.ord = ord;
    }
}
