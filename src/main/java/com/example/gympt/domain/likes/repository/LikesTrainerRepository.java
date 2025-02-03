package com.example.gympt.domain.likes.repository;

import com.example.gympt.domain.likes.entity.LikesGym;
import com.example.gympt.domain.likes.entity.LikesTrainers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LikesTrainerRepository extends JpaRepository<LikesTrainers, Long> {
    Boolean existsByMember_EmailAndTrainers_Member_email(String likesMemberEmail, String likesTrainerEmail);

    @Query("select t.trainers from LikesTrainers t where t.member.email = :email")
    List<LikesTrainers> findLikesTrainersByMemberEmail(@Param("email") String email);

    @Modifying
    @Transactional
    @Query("delete from LikesTrainers t where t.member.email = :email and t.trainers.member.email = :trainer_email")
    void deleteTrainerEmail(@Param("email") String email, @Param("trainer_email") String trainerEmail);

}
