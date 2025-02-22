package com.example.gympt.domain.community.repository;

import com.example.gympt.domain.community.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Query("SELECT c FROM Community c WHERE c.title LIKE %:keyword% OR c.content LIKE %:keyword%")
    List<Community> searchByKeyword(@Param("keyword") String keyword);
}
