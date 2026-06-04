package com.toiletfinder.toilet_finder.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain
    ) throws ServletException, IOException {

        String header =
                request.getHeader(
                        "Authorization"
                );

        if (header == null
                || !header.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                header.substring(7);

        try {

            UUID userId =
                    jwtService.extractUserId(
                            token
                    );

            UsernamePasswordAuthenticationToken auth =

                    new UsernamePasswordAuthenticationToken(

                            userId,

                            null,

                            Collections.emptyList()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(auth);

        } catch (JwtException e) {

            log.debug(
                    "JWT validation failed. path={}, reason={}",
                    request.getServletPath(),
                    e.getClass().getSimpleName()
            );

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path =
                request.getServletPath();

        return path.startsWith("/auth/register")
                || path.startsWith("/auth/login")
                || path.startsWith("/auth/refresh")
                || path.equals("/toilets/nearby")
                || path.startsWith("/actuator/")
                || path.equals("/health")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.endsWith(".html")
                || path.startsWith("/js")
                || path.startsWith("/css");
    }
}
