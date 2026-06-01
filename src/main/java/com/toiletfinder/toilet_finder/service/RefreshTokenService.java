package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.auth.RefreshTokenResult;
import com.toiletfinder.toilet_finder.exception.InvalidRefreshTokenException;
import com.toiletfinder.toilet_finder.model.RefreshToken;
import com.toiletfinder.toilet_finder.repository.RefreshTokenRepository;
import com.toiletfinder.toilet_finder.security.TokenHashService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository
            refreshTokenRepository;

    private final TokenHashService tokenHashService;

    private static final Logger log =
            LoggerFactory.getLogger(
                    RefreshTokenService.class
            );

    public RefreshTokenResult createRefreshToken(
            UUID userId
    ) {

        String rawToken =
                UUID.randomUUID()
                        .toString();

        String tokenHash =
                tokenHashService.hash(
                        rawToken
                );

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setId(
                UUID.randomUUID()
        );

        refreshToken.setUserId(
                userId
        );

        refreshToken.setTokenHash(
                tokenHash
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

        log.info(
                "Refresh token created for userId={}",
                userId
        );

        return new RefreshTokenResult(

                rawToken,

                refreshToken
        );
    }

    public RefreshToken validateRefreshToken(
            String token
    ) {

        String tokenHash =
                tokenHashService.hash(
                        token
                );

        RefreshToken refreshToken =

                refreshTokenRepository
                        .findByTokenHash(
                                tokenHash
                        );

        if (refreshToken == null) {

            throw new InvalidRefreshTokenException(
                    "Refresh token not found"
            );
        }

        if (refreshToken.isRevoked()) {

            throw new InvalidRefreshTokenException(
                    "Refresh token revoked"
            );
        }

        if (refreshToken.getExpiresAt()
                .isBefore(LocalDateTime.now())) {


            throw new InvalidRefreshTokenException(
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

        log.info(
                "Refresh token revoked: tokenId={}",
                tokenId
        );
    }
}