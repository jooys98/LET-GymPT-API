package com.example.gympt.domain.trainer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Schema(description ="admin 의 트레이너 신청 목록 확인 용 dto 입니다.")
public class TrainerSaveFormDTO {
    private Long id;
    private String email;
    private String name;
    private String introduction;
    private Long age;
    private String local;
    private String gymName;
    private String gender;
    private String profileImage;
    private List<String> imageList;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime modifiedAt;
}
