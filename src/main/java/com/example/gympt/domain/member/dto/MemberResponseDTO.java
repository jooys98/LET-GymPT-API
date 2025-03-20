package com.example.gympt.domain.member.dto;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class MemberResponseDTO {
    private String email;
    private String name;
    private String phone;
    private String gender;
    private String roleName;
    private String address;
    private String localName;
    private String profileImage;
    private int reviewCount;

    public static MemberResponseDTO from(Member member) {
        return MemberResponseDTO.builder()
                .email(member.getEmail())
                .phone(member.getPhone())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .localName(member.getLocalName())
                .gender(member.getGender().toString())
                .address(member.getAddress())
                .roleName(member.getMemberRoleList().stream().map(MemberRole::name).findFirst().orElse(null))
                .reviewCount(member.reviewCount())
                .build();
    }


}
