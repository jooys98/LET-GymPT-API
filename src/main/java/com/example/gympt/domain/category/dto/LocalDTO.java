package com.example.gympt.domain.category.dto;

import com.example.gympt.domain.category.entity.Local;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString

public class LocalDTO {
    private Long id;
    private String localName;

    public static LocalDTO from(Local local) {
        return LocalDTO.builder()
                .id(local.getId())
                .localName(local.getLocalName())
                .build();
    }
}
