package com.example.gympt.domain.community.repository.querydsl;

import com.example.gympt.domain.community.entity.Community;
import com.example.gympt.domain.community.entity.QComment;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.gympt.domain.community.entity.QComment.comment;
import static com.example.gympt.domain.community.entity.QCommunity.community;

@RequiredArgsConstructor
public class CommunityRepositoryCustomImpl implements CommunityRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Community> findCommunities(String keyword) {
        return queryFactory
                .selectFrom(community)
                .leftJoin(community.comments, comment).fetchJoin()
                .where(containsKeyword(keyword))
                .distinct()
                .orderBy(community.createdAt.desc())
                .fetch();
    }

    @Override
    public List<Community> findPopularCommunity() {
        return queryFactory
                .selectFrom(community)
                .where(isPopular())
                .orderBy(community.createdAt.desc())
                .fetch();
    }

    private BooleanExpression isPopular() {
        return community.commentCount.goe(10).or(community.views.goe(10));
    } // 댓글수 / 조회수 둘 중 하나라도 10 개 이상인 글들

    private BooleanExpression containsKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return community.content.contains(keyword)
                .or(community.title.contains(keyword))
                .or(comment.content.contains(keyword)); // 댓글 내용도 검색 조건에 추가
    }
}
