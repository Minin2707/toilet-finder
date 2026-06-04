package com.toiletfinder.toilet_finder.config;

import com.toiletfinder.toilet_finder.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers

                        .contentTypeOptions(
                                Customizer.withDefaults()
                        )

                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny
                        )

                        .referrerPolicy(referrer ->
                                referrer.policy(
                                        org.springframework.security.web.header.writers
                                                .ReferrerPolicyHeaderWriter
                                                .ReferrerPolicy
                                                .STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                                )
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/auth/register/**",
                                "/auth/login/**",
                                "/auth/refresh",
                                "/health",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/photos/**",
                                "/**/*.html",
                                "/**/*.js",
                                "/**/*.css"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
