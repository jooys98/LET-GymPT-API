package com.example.gympt.domain.likes.entity;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.trainer.entity.Trainers;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "likes_trainer_tbl")
public class LikesTrainers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_email")
    private Trainers trainers;

    public static LikesTrainers createLikes(Member member, Trainers trainers) {
        return LikesTrainers.builder()
                .member(member)
                .trainers(trainers)
                .build(); //멤버객체 + 트레이너  -> 빌더 -> 라이크 객체
    }
}
