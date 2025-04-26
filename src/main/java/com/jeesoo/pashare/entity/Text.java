package com.jeesoo.pashare.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "text")
public class Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // Large Object 처리
    @Column(length = 20000) // 최대 20,000자 제한
    private String text;

    private String passcode;
    
    private String shareId; // 링크 공유용 랜덤 ID
    
    private LocalDateTime expiryTime; // 만료 시간
    
    private boolean isLinkShared; // 링크 공유 여부
}
