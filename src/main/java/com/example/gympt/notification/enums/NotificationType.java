package com.example.gympt.notification.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    //to trainer
    TRAINER_APPROVAL("트레이너 승인"),
    PRICE_UPDATE("역경매 가격 업데이트"),
    OPEN_AUCTION("새 역경매 신청이 있습니다"),
    SELECTED_AUCTION("역경매 최종 낙찰"),
    ENDED_AUCTION("역경매 종료"),

    //to member
    NEW_AUCTION_TRAINER("새 트레이너가 입찰 하였습니다"),
    NEW_MESSAGE("새 메세지 도착"),
    BOOKING_DAY("예약일 입니다, 늦지 않게 방문해주세요😀"),
    REVIEW_APPROVED("리뷰가 승인되었습니다"),
    REVIEW_REJECTED("리뷰에 부적절한 내용이 감지되었습니다"),
    PROCESSING_ERROR("리뷰 검토 중 오류가 발생했습니다");
    private final String description;
}
