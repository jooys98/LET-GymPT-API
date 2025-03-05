package com.example.gympt.notification.entity;


import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.entity.BaseEntity;
import com.example.gympt.notification.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_email", referencedColumnName = "email")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    private String fcmToken;

    private boolean isRead;

    public static Notification of(NotificationType type,
                                  Member target,
                                  String title,
                                  String body,
                                  String fcmToken) {
        return Notification.builder()
                .type(type)
                .member(target)
                .title(title)
                .body(body)
                .fcmToken(fcmToken)
                .isRead(false)
                .build();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
