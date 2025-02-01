package com.example.gympt.domain.category.entity;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.likes.entity.Likes;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "local_tbl")
public class Local {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String localName;

   @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gym> gym = new ArrayList<>();

}
