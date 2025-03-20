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
@Table(name = "likes_gym_tbl")
public class LikesGym {
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



    public static LikesGym from(Member member, Gym gym) {
        return LikesGym.builder()
                .member(member)
                .gym(gym)
                .build(); //멤버객체 + 헬스장 -> 빌더 -> 라이크 객체
    }
}
