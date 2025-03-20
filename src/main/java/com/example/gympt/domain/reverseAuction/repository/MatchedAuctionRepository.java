package com.example.gympt.domain.reverseAuction.repository;

import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchedAuctionRepository extends JpaRepository<MatchedAuction, Long> {

    @Query("select t from MatchedAuction t where t.auctionTrainerBid.trainer.member.email=:email")
    List<MatchedAuction> findByTrainerEmail(@Param("email") String email);

    @Query("select m from MatchedAuction m where m.auctionRequest.member.email =:email order by m.createdAt desc")
    List<MatchedAuction> findByEmail(@Param("email") String email);
}
