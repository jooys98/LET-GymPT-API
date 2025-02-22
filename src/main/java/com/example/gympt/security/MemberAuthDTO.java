package com.example.gympt.security;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;

public class MemberAuthDTO extends User {
 // 유저 인증 객체

    private String email;
    private String password;
    private String name;
    private List<String> roleNames = new ArrayList<>();



    public MemberAuthDTO(String email, String password, String name, List<String> roleNames) {
        // ROLE_ 접두사를 붙여서 권한을 부여
        super(email, password, roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).toList());
        this.email = email;
        this.password = password;
        this.name = name;
        this.roleNames = roleNames;
    }

    public Map<String, Object> getClaims() {

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", this.email);
        dataMap.put("password", this.password);
        dataMap.put("name", this.name);
        dataMap.put("roleNames", this.roleNames);

        return dataMap;
    }
}
