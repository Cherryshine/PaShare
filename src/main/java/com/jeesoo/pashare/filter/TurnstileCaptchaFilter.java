package com.jeesoo.pashare.filter;

import com.jeesoo.pashare.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TurnstileCaptchaFilter implements Filter {

    @Value("${turnstile.secret-key}")
    private String secretKey;

    @Value("${turnstile.verify-url}")
    private String verifyUrl;

    private JwtUtil jwtUtil;
    
    // 필터에서 제외할 경로 목록
    private final Map<String, Boolean> excludePaths = new ConcurrentHashMap<>();

    public TurnstileCaptchaFilter() {
        // 캡차 페이지, 정적 리소스 및 일부 API 엔드포인트는 필터에서 제외
        excludePaths.put("/turnstile", true); // 캡차 페이지 자체
        excludePaths.put("/api/verify-captcha", true); // 캡차 검증 API
        excludePaths.put("/api/get-text", true); // 텍스트 조회 API
        excludePaths.put("/pashare_thumbnail.jpg", true);
        excludePaths.put("/apple-touch-icon.png", true);
        excludePaths.put("/android-chrome-512x512.png", true);
        excludePaths.put("/android-chrome-192x192.png", true);
        excludePaths.put("/favicon-16x16.png", true);
        excludePaths.put("/favicon-32x32.png", true);
        excludePaths.put("/site.webmanifest", true);
        excludePaths.put("/css/", true);                          // 정적 경로들
        excludePaths.put("/js/", true);
        excludePaths.put("/images/", true);
        excludePaths.put("/favicon.ico", true);

    }
    
    // JwtUtil setter 메서드 추가
    public void setJwtUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        
        // 모든 요청 로깅
        System.out.println("[Request] " + method + " " + path);
        
        // 제외 경로인 경우 필터링 없이 통과
        if (isExcludedPath(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 쿠키에서 JWT 토큰 확인
        Cookie[] cookies = httpRequest.getCookies();
        String jwtToken = null;
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("captcha_token".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }
        
        // JWT 토큰 검증
        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            // JWT 토큰이 유효한 경우 원래 요청 처리
            chain.doFilter(request, response);
        } else {
            // API 요청인지 확인 (AJAX 요청)
            // 공유 링크 URL(/api/s/)은 일반 페이지 요청으로 처리
            boolean isAjaxRequest = "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With"))
                    || (path.startsWith("/api/") && !path.startsWith("/api/s/"));
            
            if (isAjaxRequest) {
                // AJAX 요청인 경우 401 Unauthorized 반환
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                httpResponse.getWriter().write("{\"error\":\"Captcha verification required\"}");
            } else {
                // 현재 URL을 쿼리 파라미터로 인코딩하여 캡차 페이지로 리다이렉트
                String currentUrl = httpRequest.getRequestURL().toString();
                String queryString = httpRequest.getQueryString();
                
                if (queryString != null && !queryString.isEmpty()) {
                    currentUrl += "?" + queryString;
                }
                
                // 메인 페이지(/)인 경우 redirectUrl 파라미터 없이 리다이렉트
                if ("/".equals(path)) {
                    httpResponse.sendRedirect("/turnstile");
                } else {
                    // 현재 URL을 redirectUrl 파라미터로 전달
                    String encodedUrl = URLEncoder.encode(currentUrl, StandardCharsets.UTF_8.toString());
                    httpResponse.sendRedirect("/turnstile?redirectUrl=" + encodedUrl);
                }
            }
        }
    }

    private boolean isExcludedPath(String path) {
        if (excludePaths.containsKey(path)) {
            return true;
        }
        
        // 경로 패턴 검사 (예: /css/, /js/ 등으로 시작하는 모든 경로)
        return excludePaths.keySet().stream()
                .filter(pattern -> pattern.endsWith("/"))
                .anyMatch(path::startsWith);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
} 