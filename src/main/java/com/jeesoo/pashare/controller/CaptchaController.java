package com.jeesoo.pashare.controller;

import com.jeesoo.pashare.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CaptchaController {
    
    @Value("${turnstile.secret-key}")
    private String secretKey;
    
    @Value("${turnstile.verify-url}")
    private String verifyUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Autowired
    private JwtUtil jwtUtil;
    
    // 턴스타일 캡차 페이지 제공
    @GetMapping("/turnstile")
    public String turnstilePage() {
        // 쿼리 파라미터를 통해 정보를 전달받으므로 세션 사용 제거
        return "turnstile";
    }
    
    // 캡차 검증 API
    @PostMapping("/api/verify-captcha")
    @ResponseBody
    public Map<String, Object> verifyCaptcha(@RequestBody Map<String, String> request, HttpServletResponse response, HttpServletRequest httpRequest) {
        Map<String, Object> result = new HashMap<>();
        String token = request.get("token");
        String redirectUrl = request.get("redirectUrl");
        String autoShare = request.get("autoShare");
        String linkShare = request.get("linkShare");
        
        if (token == null || token.isEmpty()) {
            result.put("success", false);
            result.put("message", "토큰이 없습니다");
            return result;
        }
        
        // Cloudflare Turnstile API 검증 요청
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("secret", secretKey);
        requestBody.put("response", token);
        requestBody.put("remoteip", httpRequest.getRemoteAddr());
        
        try {
            Map<String, Object> verifyResponse = restTemplate.postForObject(
                    verifyUrl, 
                    requestBody, 
                    Map.class
            );
            
            boolean isSuccess = verifyResponse != null && Boolean.TRUE.equals(verifyResponse.get("success"));
            
            if (isSuccess) {
                // JWT 토큰 생성
                String jwtToken = jwtUtil.generateToken();
                
                // JWT 토큰을 쿠키에 설정
                Cookie cookie = new Cookie("captcha_token", jwtToken);
                cookie.setPath("/");
                cookie.setHttpOnly(true); // JavaScript에서 접근 불가능하게 설정
                cookie.setMaxAge(30 * 60); // 30분 유효
                response.addCookie(cookie);
                
                // 리다이렉트 URL 설정 (세션 대신 쿼리 파라미터 사용)
                if (redirectUrl == null || redirectUrl.isEmpty()) {
                    redirectUrl = "/";
                }
                
                // 자동 공유 파라미터 추가
                if ("true".equals(autoShare)) {
                    if (redirectUrl.contains("?")) {
                        redirectUrl += "&autoShare=true";
                    } else {
                        redirectUrl += "?autoShare=true";
                    }
                    
                    // 링크 공유 파라미터 추가
                    if ("true".equals(linkShare)) {
                        redirectUrl += "&linkShare=true";
                    }
                }
                
                result.put("success", true);
                result.put("redirectUrl", redirectUrl);
            } else {
                result.put("success", false);
                result.put("message", "캡차 검증에 실패했습니다");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "검증 과정에서 오류가 발생했습니다");
        }
        
        return result;
    }
} 