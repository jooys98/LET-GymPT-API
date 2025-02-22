package com.example.gympt.domain.community.repository;

import com.example.gympt.domain.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>{

    @Query("select c from Comment c where c.community.id =:id")
    List<Comment> findByCommunityId(@Param("id") Long id);
}
