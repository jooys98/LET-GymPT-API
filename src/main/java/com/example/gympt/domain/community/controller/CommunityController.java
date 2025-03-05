package com.example.gympt.domain.community.controller;


import com.example.gympt.domain.community.dto.CommentDTO;
import com.example.gympt.domain.community.dto.CommunityRequestDTO;
import com.example.gympt.domain.community.dto.CommunityResponseDTO;
import com.example.gympt.domain.community.service.CommunityService;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class CommunityController {

    private final CommunityService communityService;

    //모든 글 보가
    @GetMapping
    public ResponseEntity<List<CommunityResponseDTO>> getCommunityList() {
        return ResponseEntity.ok(communityService.getAllPosts());
    }

    //글 상세보기 (댓글까지)
    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDTO> getCommunityDetail(Long id) {
        return ResponseEntity.ok(communityService.getPostDetail(id));
    }


    //커뮤니티 게시글 검색
    @GetMapping("/search")
    public ResponseEntity<List<CommunityResponseDTO>> searchCommunity(String keyword) {
        return ResponseEntity.ok(communityService.getSearchPost(keyword));
    }

    //커뮤니티 글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteMyCommunity(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, Long id) {
        return ResponseEntity.ok(communityService.deletePost(memberAuthDTO.getUsername(), id));
    }

    //커뮤니티 글쓰기
    @PostMapping
    public ResponseEntity<String> createMyCommunity(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody CommunityRequestDTO communityRequestDTO) {
        communityService.createPost(memberAuthDTO.getUsername(), communityRequestDTO);
        return ResponseEntity.ok("글 작성이 완료 되었습니다!");
    }


    @GetMapping("/comments/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentByPost(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getComments(id));

    }

    //게시글에 댓글 달기
    @PostMapping("/comments")
    public ResponseEntity<String> createMyComment(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, CommentDTO commentDTO) {
        communityService.createComment(memberAuthDTO.getUsername(), commentDTO);
        return ResponseEntity.ok("댓글 작성 완료 되었습니다");

    }

    //게시글 댓글 삭제
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Long> deleteMyComment(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @PathVariable Long id) {
        return ResponseEntity.ok(communityService.deleteComment(memberAuthDTO.getUsername(), id));
    }

}
