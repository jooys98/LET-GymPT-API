package com.example.gympt.domain.member.entity;

import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Setter
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    private String email;
    private String name;

    private String password;

    private String phone;
    private String address;
    private String localName;
    private String gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    private boolean delFlag;

    public void changeDel(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public void addRole(MemberRole memberRole) {
        memberRoleList.add(memberRole);

        if (memberRole == MemberRole.TRAINER) {
            Trainers trainer = Trainers.builder()
                    .member(this)
                    .build();
            this.trainer = trainer;
            new SimpleGrantedAuthority("PREPARATION_TRAINER");
        } else if (memberRole == MemberRole.ADMIN) {
            new SimpleGrantedAuthority("ROLE_ADMIN");
        } else {
            new SimpleGrantedAuthority("ROLE_USER");
        }
    }



    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_role_list", joinColumns = @JoinColumn(name = "email"))
    @Column(name = "role") // 해당 memberRoleList 를 저장할 컬럼명을 지정
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Trainers trainer;

    @OneToOne(mappedBy = "member" ,cascade = CascadeType.ALL)
    private AuctionRequest auctionRequest;

}
