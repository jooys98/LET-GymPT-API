package com.example.gympt.domain.reverseAuction.repository;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;
import com.example.gympt.domain.reverseAuction.repository.querydsl.AuctionTrainerBidRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuctionTrainerBidRepository extends JpaRepository<AuctionTrainerBid, Long>, AuctionTrainerBidRepositoryCustom {

    @Query("SELECT atb FROM AuctionTrainerBid atb " +
            "WHERE atb.auctionRequest.id = :auctionRequestId " +
            "AND atb.trainer.member.email = :trainerEmail order by atb.createdAt desc ")
    Optional<AuctionTrainerBid> findByAuctionRequestIdAndTrainer(
            @Param("auctionRequestId") Long auctionRequestId,
            @Param("trainerEmail") String trainerEmail
    );
//
//    @Query("select t from AuctionTrainerBid t where t.trainer.id =:trainerId")
//    Optional<AuctionTrainerBid> findByTrainerId(@Param("trainerId") Long trainerId);


    @Query("select a from AuctionTrainerBid a where a.auctionRequest.member.email=:email order by a.createdAt desc")
    List<AuctionTrainerBid> findByMemberEmail(@Param("email") String memberEmail);

    @Modifying
    @Query("delete from AuctionTrainerBid a where a.id in :ids")
    void deleteByIdList(@Param("ids") List<Long> ids);


    @Query("select t from AuctionTrainerBid t where t.auctionRequest.id =:auctionRequestId order by t.createdAt desc")
    List<AuctionTrainerBid> findTrainersInAuction(@Param("auctionRequestId") Long auctionRequestId);

    @Query("select t from AuctionTrainerBid t where t.trainer.member.email =:email order by t.createdAt desc")
    List<AuctionTrainerBid> findByTrainerEmail(@Param("email") String email);
}