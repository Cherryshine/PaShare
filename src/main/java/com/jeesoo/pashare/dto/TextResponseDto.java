package com.jeesoo.pashare.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TextResponseDto {
    private String message;
    private String text;
    private LocalDateTime expiryTime;

    public TextResponseDto(String message, String text) {
        this.message = message;
        this.text = text;
    }
    
    public TextResponseDto(String message, String text, LocalDateTime expiryTime) {
        this.message = message;
        this.text = text;
        this.expiryTime = expiryTime;
    }
}