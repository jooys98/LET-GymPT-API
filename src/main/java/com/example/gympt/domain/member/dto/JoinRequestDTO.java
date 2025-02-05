package com.example.gympt.domain.member.dto;

import com.example.gympt.domain.member.enums.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class JoinRequestDTO {

    @NotBlank(message = "유저이름은 필수 입력 값입니다.")
    private String name;
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    private String email;
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;
    private String address;
    private String gender;
    private String localName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private MemberRole role;
}
