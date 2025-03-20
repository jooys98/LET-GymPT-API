package com.example.gympt.domain.community.controller;


import com.example.gympt.domain.chat.dto.ChatRoomDTO;
import com.example.gympt.domain.community.dto.CommentDTO;
import com.example.gympt.domain.community.dto.CommunityRequestDTO;
import com.example.gympt.domain.community.dto.CommunityResponseDTO;
import com.example.gympt.domain.community.service.CommunityService;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Community API", description = "커뮤니티 보기 , 쓰기 , 댓글 관련 기능을 제공하는 API")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "모든 글 조회", description = "등록된 모든 커뮤니티 글을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CommunityResponseDTO>> getCommunityList() {
        return ResponseEntity.ok(communityService.getAllPosts());
    }


    @Operation(summary = "게시글 상세 조회", description = "특정 ID의 게시글과 해당 게시글의 댓글을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CommunityResponseDTO> getCommunityDetail(@Parameter(description = "조회할 게시글 ID") @PathVariable Long id) {
        return ResponseEntity.ok(communityService.getPostDetail(id));
    }

    @Operation(summary = "인기 글 목록 조회", description = "댓글 수와 조회 수를 기준으로 인기 글을 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<List<CommunityResponseDTO>> getPopularCommunityList() {
        return ResponseEntity.ok(communityService.getAllPopularPosts());
    }


    @Operation(summary = "커뮤니티 글 검색", description = "키워드를 사용하여 게시글을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<List<CommunityResponseDTO>> searchCommunity(@Parameter(description = "검색 키워드") @RequestParam String keyword) {
        return ResponseEntity.ok(communityService.getSearchPost(keyword));
    }

    @Operation(summary = "게시글 삭제", description = "본인이 작성한 게시글을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteMyCommunity(@Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal MemberAuthDTO memberAuthDTO,
                                                  @Parameter(description = "삭제할 게시글 ID") @PathVariable Long id) {
        return ResponseEntity.ok(communityService.deletePost(memberAuthDTO.getUsername(), id));
    }

    @Operation(
            summary = "커뮤니티 글 작성하기",
            description = "유저의 인증정보와 게시글 작성 request 를 받아서 게시글을 저장합니다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "유저의 작성 글,닉네임,이미지 등이 포함됩니다",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CommunityRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "글 작성 성공")
            }
    )
    @PostMapping
    public ResponseEntity<String> createMyCommunity(@Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal MemberAuthDTO memberAuthDTO,
                                                     CommunityRequestDTO communityRequestDTO) {
        communityService.createPost(memberAuthDTO.getUsername(), communityRequestDTO);
        return ResponseEntity.ok("글 작성이 완료 되었습니다!");
    }

    @Operation(summary = "게시글에 해당하는 댓글들 조회 ")
    @GetMapping("/comments/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentByPost(@Parameter(description = "댓글을 조회할 특정 게시글의 ID") @PathVariable Long id) {
        return ResponseEntity.ok(communityService.getComments(id));

    }

    @Operation(
            summary = "커뮤니티 글에 댓글 작성하기",
            description = "유저의 인증정보와 댓글 작성 request 를 받아서 게시글을 저장합니다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "작성 댓글 ,닉네임, 댓글이 달릴 커뮤니티 ID 등이 포함됩니다",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CommentDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 작성 성공")
            }
    )
    @PostMapping("/comments")
    public ResponseEntity<String> createMyComment(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                  @AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody CommentDTO commentDTO) {
        communityService.createComment(memberAuthDTO.getUsername(), commentDTO);
        return ResponseEntity.ok("댓글 작성 완료 되었습니다");

    }

    @Operation(summary = "댓글 삭제", description = "본인이 작성한 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Long> deleteMyComment(@Parameter(description = "인증된 사용자 정보", hidden = true)
                                                    @AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @Parameter(description = "삭제할 댓글의 Id")@PathVariable Long id) {
        return ResponseEntity.ok(communityService.deleteComment(memberAuthDTO.getUsername(), id));
    }

}
