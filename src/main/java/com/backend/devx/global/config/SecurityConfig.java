package com.backend.devx.global.config;

import com.backend.devx.global.jwt.JwtAccessDeniedHandler;
import com.backend.devx.global.jwt.JwtAuthenticationEntryPoint;
import com.backend.devx.member.dto.jwt.CustomJwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity // 기본 웹 보안 활성화
@EnableMethodSecurity //@PreAuthorize 어노테이션 활성화
public class SecurityConfig {

    @Autowired
    private CorsFilter corsFilter;

    @Autowired
    private CustomJwtFilter customJwtFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(c -> {
                    c.authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedHandler(jwtAccessDeniedHandler);
                })
                .authorizeHttpRequests(c -> {
                    c.requestMatchers(
                            "/h2-console/*",
                                    "/api/login",
                                    "/api/signup",
                                    "/api/member/exists/**", "/h2-console/**",
                                    "/js/**",
                                    "/css/**",
                                    "/image/**",
                                    "/fonts/**",
                                    "/favicon.ico").permitAll()
                            .anyRequest().authenticated();
                });
        http.headers().frameOptions().sameOrigin();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
