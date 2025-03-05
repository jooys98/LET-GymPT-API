package com.example.gympt.domain.community.repository;

import com.example.gympt.domain.community.dto.CommentDTO;
import com.example.gympt.domain.community.entity.Community;
import com.example.gympt.domain.community.repository.querydsl.CommunityRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long>, CommunityRepositoryCustom {

}
