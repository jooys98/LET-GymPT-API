package com.example.gympt.domain.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MemberResponseDTO {
    private String email;
    private String name;
    private String phone;
    private String gender;
    private String address;
    private String localName;
    private String profileImage;
}
