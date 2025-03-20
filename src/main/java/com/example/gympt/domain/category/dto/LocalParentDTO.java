package com.example.gympt.domain.category.dto;

import com.example.gympt.domain.category.entity.Local;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Setter

@Schema(description = "지역(Local) 정보 DTO, 계층 구조 지원")
public class LocalParentDTO {
    private Long id;
    private String localName;
    @Schema(description = "하위 지역 목록" ,example = "관악구 - {봉천동 - 서울대입구}")
    private Set<LocalParentDTO> children = new HashSet<>();

    public static LocalParentDTO from(Local local) {
        LocalParentDTO dto = new LocalParentDTO();
        dto.setId(local.getId());
        dto.setLocalName(local.getLocalName());
        if (local.getChildren() != null && !local.getChildren().isEmpty()) {
            dto.setChildren(local.getChildren().stream().map(LocalParentDTO::from).collect(Collectors.toSet()));
        }

        return dto;
    }
}

