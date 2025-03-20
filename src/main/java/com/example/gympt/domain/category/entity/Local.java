package com.example.gympt.domain.category.entity;

import com.example.gympt.domain.category.entity.enums.LastType;
import com.example.gympt.domain.gym.entity.Gym;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString(exclude = {"parent", "children"})
@Table(name = "local_tbl")
public class Local {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String localName;

    //여러개의 부모 카테고리 들도 또 하나의 부모 카테고리를 가질 수 있음
    @ManyToOne(fetch = FetchType.LAZY)

    //각 카테고리는 자신을 참조하는 parent_id 필드를 가짐
    //이 parent_id는 같은 테이블의 id를 참조
    @JoinColumn(name="parent_id")
    private Local parent;

    //자식은 부모를 참조 하는 리스트
    //하나의 카테고리는 여러 자식 카테고리를 만들 수 있음
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Local> children = new ArrayList<>();

    //엔티티 증식의 끝을 나태니기 위한 enums 값
    //Y : 이에 해당하는 카테고리는 자식 카테고리가 없음
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'") // 자식 카테고리 존재
    private LastType lastType;

    @OneToMany(mappedBy = "local")
    @Builder.Default
    private List<LocalGymBridge> localGymBridgeList = new ArrayList<>();


}
