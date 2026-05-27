package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.model.RefreshToken;
import com.toiletfinder.toilet_finder.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository
            refreshTokenRepository;

    public RefreshToken createRefreshToken(
            UUID userId
    ) {

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setId(
                UUID.randomUUID()
        );

        refreshToken.setUserId(
                userId
        );

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setCreatedAt(
                LocalDateTime.now()
        );

        refreshToken.setExpiresAt(

                LocalDateTime.now()
                        .plusDays(30)
        );

        refreshToken.setRevoked(false);

        refreshTokenRepository.save(
                refreshToken
        );

        return refreshToken;
    }

    public RefreshToken validateRefreshToken(
            String token
    ) {

        RefreshToken refreshToken =

                refreshTokenRepository
                        .findByToken(token);

        if (refreshToken == null) {

            throw new RuntimeException(
                    "Refresh token not found"
            );
        }

        if (refreshToken.isRevoked()) {

            throw new RuntimeException(
                    "Refresh token revoked"
            );
        }

        if (refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        return refreshToken;
    }

    public void revokeToken(
            UUID tokenId
    ) {

        refreshTokenRepository
                .revokeToken(tokenId);
    }
}