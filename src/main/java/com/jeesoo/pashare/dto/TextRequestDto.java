package com.jeesoo.pashare.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextRequestDto {

    @Size(max = 20000, message = "Text cannot exceed 20,000 characters.") // 최대 20,000자 제한
    private String text;

    private String passcode;
}
