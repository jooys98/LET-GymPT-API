package com.example.gympt.domain.category.entity;

import com.example.gympt.domain.gym.entity.Gym;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "local_bridge")
@ToString(exclude = {"local", "gym"})

public class LocalGymBridge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;


    public static LocalGymBridge from(Local local, Gym gym) {
        return LocalGymBridge.builder()
                .local(local)
                .gym(gym)
                .build();
    }
}
