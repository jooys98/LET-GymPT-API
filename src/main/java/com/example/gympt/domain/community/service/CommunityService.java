package com.example.gympt.domain.community.service;

import com.example.gympt.domain.community.dto.CommentDTO;
import com.example.gympt.domain.community.dto.CommunityRequestDTO;
import com.example.gympt.domain.community.dto.CommunityResponseDTO;
import com.example.gympt.domain.community.entity.Comment;
import com.example.gympt.domain.community.entity.Community;
import com.example.gympt.domain.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface CommunityService {
    List<CommunityResponseDTO> getAllPosts();

    CommunityResponseDTO getPostDetail(Long id);

    List<CommunityResponseDTO> getSearchPost(String keyword);

    Long deletePost(String username, Long id);

    void createPost(String username, CommunityRequestDTO communityRequestDTO);

    void createComment(String username, CommentDTO commentDTO);

    Long deleteComment(String username, Long id);

    List<CommentDTO> getComments(Long id);

    List<CommunityResponseDTO> getAllPopularPosts();

}
