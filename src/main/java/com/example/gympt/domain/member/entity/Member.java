package com.example.gympt.domain.member.entity;

import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.enums.Gender;
import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

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
    private Gender gender;

    private Long birthday;
    private String profileImage;
    private boolean delFlag;
    @Column
    private String fcmToken;


    public void changeDel(boolean delFlag) {
        this.delFlag = delFlag;
    }

    public void addRole(MemberRole memberRole) {
        memberRoleList.add(memberRole);
    }


    public void addGender(String gender) {
        this.gender = gender.equals("M") ? Gender.M : Gender.F;
    }

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_role_list", joinColumns = @JoinColumn(name = "email"))
    @Column(name = "role") // 해당 memberRoleList 를 저장할 컬럼명을 지정
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Trainers trainer;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private AuctionRequest auctionRequest;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Review> reviewList = new ArrayList<>();


    public void updateName(String name) {
        this.name = name;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateLocalName(String localName) {
        this.localName = localName;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public int reviewCount() {
       return this.reviewList.size();
    }

    public static Member from(JoinRequestDTO request) {
        Member member = Member.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(request.getPassword())
                .birthday(request.getBirthday())
                .profileImage("https://blog.kakaocdn.net/dn/GHYFr/btrsSwcSDQV/UQZxkayGyAXrPACyf0MaV1/img.jpg")
                .phone(request.getPhone())
                .address(request.getAddress())
                .localName(request.getLocalName())
                .build();
        member.addGender(request.getGender());

        String role = request.getRole();
        if (role.equals("TRAINER")) {
            member.addRole(MemberRole.PREPARATION_TRAINER);
        } else if (role.equals("USER")) {
            member.addRole(MemberRole.USER);
        } else if (role.equals("ADMIN")) {
            member.addRole(MemberRole.ADMIN);
        }
        return member;

    }
}
