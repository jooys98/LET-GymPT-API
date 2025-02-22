package com.example.gympt.domain.community.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class CommentDTO {
    private Long postId;
    private String content;
    private String name;
    private LocalDateTime createdAt;
}
