package com.example.gympt.domain.community.repository.querydsl;

import com.example.gympt.domain.community.entity.Community;

import java.util.Collection;
import java.util.List;

public interface CommunityRepositoryCustom {
    List<Community> findCommunities(String keyword);
}
