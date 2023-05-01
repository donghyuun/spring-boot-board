package com.example.sbb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration //스프링의 환경 설정 파일임을 의미, 여기선 스프링 시큐리티 설정을 위해 사용됨
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 함
                   // 내부적으로 SpringSecurityFilterChain이 동작하여 URL필터가 적용됨
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().requestMatchers(
                new AntPathRequestMatcher("/**")//모든 인증되지 않은 요청을 허가
        ).permitAll()
                .and()//H2 콘솔 예외 처리(CSRF 검증 안하도록)
                    .csrf().ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                .and()
                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(
                        XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                ));
        return http.build();
    }

    @Bean//BCryptPasswordEncoder 객체를 빈(bean)으로 등록해서 사용
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
