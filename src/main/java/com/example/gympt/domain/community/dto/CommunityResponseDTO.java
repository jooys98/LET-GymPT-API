package com.example.gympt.domain.community.dto;

import com.example.gympt.domain.community.entity.Community;
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
    private Long views;
    private Integer commentCount;

    public static CommunityResponseDTO from(Community community) {
        return CommunityResponseDTO.builder()
                .name(community.getMember().getName())
                .title(community.getTitle())
                .image(community.getImage())
                .content(community.getContent())
                .createdAt(community.getCreatedAt())
                .comments(community.getComments().stream().map(CommentDTO::from).toList())
                .views(community.getViews())
                .commentCount(community.getCommentCount())
                .build();
    }
}
