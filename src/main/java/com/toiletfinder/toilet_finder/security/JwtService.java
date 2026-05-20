package com.toiletfinder.toilet_finder.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import static java.security.KeyRep.Type.SECRET;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    public String generateToken(UUID userId) {

        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + expirationMs
                        )
                )
                .signWith(
                        Keys.hmacShaKeyFor(
                                secret.getBytes()
                        )
                )
                .compact();
    }

    public UUID extractUserId(String token) {

        String userId = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return UUID.fromString(userId);
    }
}
