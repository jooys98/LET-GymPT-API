package com.example.gympt.domain.member.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class RoleChangeRequest {
    private String email;
}
