package com.example.gympt.domain.category.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Setter
public class LocalParentDTO {
    private Long id;
    private String localName;

    private Set<LocalParentDTO> children = new HashSet<>();
}

