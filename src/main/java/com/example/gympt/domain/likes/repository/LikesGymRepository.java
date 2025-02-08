package com.example.gympt.domain.likes.repository;

import com.example.gympt.domain.likes.entity.LikesGym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

public interface LikesGymRepository extends JpaRepository<LikesGym, Long> {

    @Query("select g.gym from LikesGym g where g.member.email = :email")
    List<LikesGym> findLikesGymsByMemberEmail(@Param("email") String email);

    Boolean existsByMember_EmailAndGym_Id(String email, Long gymId);
//연관관계를 통해 검색할 시에는 _ 를 사용하여 검색해야 한다

    @Modifying
    @Query("delete from LikesGym g where g.member.email = :email and g.gym.id = :gym_id")
    void deleteEmailGymId(@Param("email") String email, @Param("gym_id") Long gymId);

}
