package com.example.gympt.domain.chat.repository;

import com.example.gympt.domain.chat.document.ChatMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessages, String> {
//jpa 레포지토리 처럼 이름규칙에 따라 자동으로 쿼리 생성

    //채팅방 id 로 조회하고 최신 시간순으로 정렬하기
    List<ChatMessages> findByRoomIdOrderBySendTimeAsc(Long roomId);

    List<ChatMessages> findByEmail(String email);

    @Query(value = "{ 'roomId': ?0 }", sort = "{ 'sendTime': -1 }")
    Optional<ChatMessages> findLastMessageByRoomId(@Param("roomId") Long roomId);

    @Aggregation(pipeline = {
            "{ $match: { 'roomId': { $in: ?0 },'isRead': false } }",
            "{ $sort: { 'sendTime': -1 } }",
            "{ $group: { " +
                    "'_id': '$roomId'," +
                    "'document': { $first: '$$CURRENT' } " +  // 현재 문서 전체를 가져옴
                    "} }",
            "{ $replaceRoot: { 'newRoot': '$document' } }"  // 원래 문서 구조로 복원
    })
    List<ChatMessages> findLastMessagesAndIsReadFalseByRoomIds(@Param("roomIds") List<Long> roomIds);


    @Aggregation(pipeline = {
            "{ $match: { 'roomId': { $in: ?0 }}}",
            "{ $sort: { 'sendTime': -1 } }",
            "{ $group: { " +
                    "'_id': '$roomId'," +
                    "'document': { $first: '$$CURRENT' } " +  // 현재 문서 전체를 가져옴
                    "} }",
            "{ $replaceRoot: { 'newRoot': '$document' } }"  // 원래 문서 구조로 복원
    })
    List<ChatMessages> findLastMessagesByRoomIds(@Param("roomIds") List<Long> roomIds);



    @Modifying
    @Query("{ 'roomId': ?0, 'email': ?1 }")  // 조건
    @Update("{ '$set': { 'isRead': true }}")  // 업데이트 내용
    void modifyIsRead(Long roomId, String email);

    @Query(value = "{ 'email': ?0 ,'isRead' : ?1}", sort = "{ 'sendTime': -1 }")
    List<ChatMessages> findByMemberAndIsRead(String email, boolean b);
}
