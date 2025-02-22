package com.example.gympt.domain.community.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class CommunityRequestDTO {

    private String content;
    private String name;
    private String title;
    private MultipartFile image;
}
