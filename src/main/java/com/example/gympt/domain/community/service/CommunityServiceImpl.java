package com.example.gympt.domain.community.service;

import com.example.gympt.domain.community.dto.CommentDTO;
import com.example.gympt.domain.community.dto.CommunityRequestDTO;
import com.example.gympt.domain.community.dto.CommunityResponseDTO;
import com.example.gympt.domain.community.entity.Comment;
import com.example.gympt.domain.community.entity.Community;
import com.example.gympt.domain.community.repository.CommentRepository;
import com.example.gympt.domain.community.repository.CommunityRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.exception.CustomNotAccessHandler;
import com.example.gympt.util.s3.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final CustomFileUtil customFileUtil;

    @Transactional(readOnly = true)
    @Override
    public List<CommunityResponseDTO> getAllPosts() {
        return communityRepository.findAll().stream().map(CommunityResponseDTO::from).toList();
    }

    @Transactional
    @Override
    public CommunityResponseDTO getPostDetail(Long id) {
        Community community = communityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Community not found"));
        community.addViews(); //게시글 상세조회 시 조회수 증가
        communityRepository.save(community);
        return CommunityResponseDTO.from(community);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommunityResponseDTO> getSearchPost(String keyword) {
        return communityRepository.findCommunities(keyword).stream().map(CommunityResponseDTO::from).toList();
//
    }

    @Override
    public void createPost(String username, CommunityRequestDTO communityRequestDTO) {
        Member member = getMember(username);
        String image = customFileUtil.uploadS3File(communityRequestDTO.getImage());
        communityRepository.save(Community.from(communityRequestDTO, member, image));
    }


    @Override
    public Long deletePost(String username, Long id) {
        Member member = getMember(username);
        Community community = getCommunity(id);
        if (!community.getMember().equals(member)) {
            throw new CustomNotAccessHandler("삭제 권한이 없습니다");
        }
        List<Comment> comment = commentRepository.findByCommunityId(id);
        customFileUtil.deleteS3File(community.getImage());
        commentRepository.deleteAll(comment);
        communityRepository.delete(community);
        return community.getId();
    }


    @Override
    public void createComment(String username, CommentDTO commentDTO) {
        Member member = getMember(username);
        Community community = getCommunity(commentDTO.getPostId());
        community.addCommentCount();
        communityRepository.save(community);
        commentRepository.save(Comment.from(community, commentDTO, member));

    }

    @Override
    public Long deleteComment(String username, Long id) {
        Member member = getMember(username);
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 댓글입니다"));
        if (!comment.getMember().equals(member)) {
            throw new CustomNotAccessHandler("삭제 권한이 없습니다");
        }
        commentRepository.delete(comment);
        return comment.getId();
    }

    @Override
    public List<CommentDTO> getComments(Long id) {
        return commentRepository.findByCommunityId(id).stream().map(CommentDTO::from).toList();
    }

    @Override
    public List<CommunityResponseDTO> getAllPopularPosts() {
        return communityRepository.findPopularCommunity().stream().map(CommunityResponseDTO::from).toList();
    }


    private Member getMember(String username) {
        return memberRepository.findByEmail(username).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다"));
    }


    private Community getCommunity(Long id) {
        return communityRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Community not found"));
    }
}
