package com.example.gympt.domain.member.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class LoginResponseDTO {
    private String email;
    private String name;
    private List<String> roles;
    private String accessToken;

}
