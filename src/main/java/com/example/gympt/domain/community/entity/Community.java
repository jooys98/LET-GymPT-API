package com.example.gympt.domain.community.entity;

import com.example.gympt.domain.community.dto.CommunityRequestDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "community_tbl")
public class Community extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Long views = 0L;

    private Integer commentCount = 0;
    private String image;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;


    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void addViews() {
        this.views += 1;
    }

    public void addCommentCount() {
        this.commentCount += 1;
    }

    public static Community from(CommunityRequestDTO communityRequestDTO, Member member, String image) {
        return Community.builder()
                .title(communityRequestDTO.getTitle())
                .content(communityRequestDTO.getContent())
                .image(image)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
