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

    default CommunityResponseDTO convertToDTO(Community community) {
        return CommunityResponseDTO.builder()
                .name(community.getMember().getName())
                .title(community.getTitle())
                .image(community.getImage())
                .content(community.getContent())
                .createdAt(community.getCreatedAt())
                .comments(community.getComments().stream().map(this::convertToCommentsDTO).toList())
                .build();
    }


    default Community convertToEntity(CommunityRequestDTO communityRequestDTO, Member member, String image) {
        return Community.builder()
                .title(communityRequestDTO.getTitle())
                .content(communityRequestDTO.getContent())
                .image(image)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();
    }

    default CommentDTO convertToCommentsDTO(Comment comment) {
        return CommentDTO.builder()
                .postId(comment.getCommunity().getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .name(comment.getMember().getName())
                .build();
    }


    default Comment convertToComment(Community community, CommentDTO commentDTO, Member member) {
        return Comment.builder()
                .member(member)
                .community(community)
                .content(commentDTO.getContent())
                .createdAt(LocalDateTime.now())
                .build();

    }
}
