package com.jeesoo.pashare.config;

import com.jeesoo.pashare.filter.TurnstileCaptchaFilter;
import com.jeesoo.pashare.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public FilterRegistrationBean<TurnstileCaptchaFilter> turnstileCaptchaFilter() {
        TurnstileCaptchaFilter filter = new TurnstileCaptchaFilter();
        filter.setJwtUtil(jwtUtil); // JwtUtil 인스턴스 직접 설정
        
        FilterRegistrationBean<TurnstileCaptchaFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*"); // 모든 경로에 필터 적용
        return registrationBean;
    }
} 