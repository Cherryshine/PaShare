package com.jeesoo.pashare.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/s/{shareId}")
    public String redirectToShareApi() {
        // 해당 경로로의 접근을 API 엔드포인트로 리다이렉트합니다
        return "forward:/api/s/{shareId}";
    }

} 