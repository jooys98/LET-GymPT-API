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
public class CommunityResponseDTO {
    private Long id;
    private String content;
    private String name;
    private String title;
    private String image;
    private LocalDateTime createdAt;
    private List<CommentDTO> comments;
}
