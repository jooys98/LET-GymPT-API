package com.example.gympt.domain.trainer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TrainerResponseDTO {
    private Long id;
    private String email;
    private String name;
    private String introduction;
    private Long age;
    private String gymName;
    private String gender;
    private String local;
    private int likesCount;
    private boolean likes;


    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 생성시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt; // 수정 시간

}
