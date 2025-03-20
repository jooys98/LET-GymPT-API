package com.example.gympt.domain.review.repository;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

   @Query("select r from Review r where r.member.email= :email")
    List<Review> findReviewsByEmail(@Param("email") String email);

   @Query("select r from Review r where r.gym.id =:gymId order by r.createdAt desc ")
    List<Review> findByGymId(@Param("gymId") Long gymId);

    void deleteByGym(Gym gym);

@Query("select tr from Review tr where tr.trainers.id =:trainerid order by tr.createdAt desc")
    List<Review> findByTrainerId(@Param("trainerId")Long trainerId);
}
