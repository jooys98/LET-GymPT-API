package com.example.gympt.domain.category.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString

public class LocalDTO {
    private Long id;
    private String localName;
}
