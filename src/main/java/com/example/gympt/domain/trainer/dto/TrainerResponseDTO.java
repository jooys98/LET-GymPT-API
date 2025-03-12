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
    private String profileImage;
    private String name;
    private String introduction;
    private Long age;
    private Long gymId;
    private String gymName;
    private String gender;
    private Long localId;
    private String local;
    private int likesCount;
    private boolean likes;
    private List<String> imageList;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 생성시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt; // 수정 시간

}
