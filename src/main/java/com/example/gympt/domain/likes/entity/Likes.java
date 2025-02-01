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
@Table(name = "likes_tbl")
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //pk

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    //fk
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "trainer_email")
   private Trainers trainers;

    public static Likes createLikes(Member member, Gym gym) {
        return Likes.builder()
                .member(member)
                .gym(gym)
                .build(); //멤버객체 + 헬스장 -> 빌더 -> 라이크 객체
    }
}
