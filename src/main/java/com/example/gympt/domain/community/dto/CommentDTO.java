package com.example.gympt.domain.community.dto;

import com.example.gympt.domain.community.entity.Comment;
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

    public static CommentDTO from(Comment comment) {
        return CommentDTO.builder()
                .postId(comment.getCommunity().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .name(comment.getMember().getName())
                .build();
    }
}
