package com.example.gympt.domain.community.entity;

import com.example.gympt.domain.community.dto.CommentDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comment_tbl")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    public static Comment from(Community community, CommentDTO commentDTO, Member member) {
        return Comment.builder()
                .member(member)
                .community(community)
                .content(commentDTO.getContent())
                .createdAt(LocalDateTime.now())
                .build();

    }
}
