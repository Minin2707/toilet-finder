package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.auth.RefreshTokenResult;
import com.toiletfinder.toilet_finder.exception.InvalidRefreshTokenException;
import com.toiletfinder.toilet_finder.model.RefreshToken;
import com.toiletfinder.toilet_finder.repository.RefreshTokenRepository;
import com.toiletfinder.toilet_finder.security.TokenHashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenHashService tokenHashService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldCreateRefreshToken() {

        UUID userId = UUID.randomUUID();

        when(
                tokenHashService.hash(any())
        ).thenReturn("hash");

        RefreshTokenResult result =
                refreshTokenService.createRefreshToken(
                        userId
                );

        assertNotNull(
                result.rawToken()
        );

        assertNotNull(
                result.refreshToken()
        );

        assertEquals(
                userId,
                result.refreshToken().getUserId()
        );

        assertEquals(
                "hash",
                result.refreshToken()
                        .getTokenHash()
        );

        assertFalse(
                result.refreshToken().isRevoked()
        );

        verify(
                refreshTokenRepository
        ).save(
                any(RefreshToken.class)
        );
    }

    @Test
    void shouldThrowWhenTokenNotFound() {

        String token = "token";

        when(
                tokenHashService.hash("token")
        ).thenReturn("hash");

        when(
                refreshTokenRepository.findByTokenHash(
                        "hash"
                )
        ).thenReturn(null);

        assertThrows(

                InvalidRefreshTokenException.class,

                () -> refreshTokenService
                        .validateRefreshToken(
                                token
                        )
        );
    }

    @Test
    void shouldThrowWhenTokenRevoked() {

        RefreshToken token =
                new RefreshToken();

        token.setRevoked(true);

        when(
                tokenHashService.hash("token")
        ).thenReturn("hash");

        when(
                refreshTokenRepository.findByTokenHash(
                        "hash"
                )
        ).thenReturn(token);

        assertThrows(

                InvalidRefreshTokenException.class,

                () -> refreshTokenService
                        .validateRefreshToken(
                                "token"
                        )
        );
    }

    @Test
    void shouldThrowWhenTokenExpired() {

        RefreshToken token =
                new RefreshToken();

        token.setRevoked(false);

        token.setExpiresAt(

                LocalDateTime.now()
                        .minusDays(1)
        );

        when(
                tokenHashService.hash("token")
        ).thenReturn("hash");

        when(
                refreshTokenRepository.findByTokenHash(
                        "hash"
                )
        ).thenReturn(token);

        assertThrows(

                InvalidRefreshTokenException.class,

                () -> refreshTokenService
                        .validateRefreshToken(
                                "token"
                        )
        );
    }

    @Test
    void shouldReturnValidRefreshToken() {

        RefreshToken token =
                new RefreshToken();

        token.setRevoked(false);

        token.setExpiresAt(

                LocalDateTime.now()
                        .plusDays(1)
        );

        when(
                tokenHashService.hash("token")
        ).thenReturn("hash");

        when(
                refreshTokenRepository.findByTokenHash(
                        "hash"
                )
        ).thenReturn(token);

        RefreshToken result =

                refreshTokenService
                        .validateRefreshToken(
                                "token"
                        );

        assertEquals(
                token,
                result
        );
    }

    @Test
    void shouldRevokeToken() {

        UUID tokenId =
                UUID.randomUUID();

        refreshTokenService.revokeToken(
                tokenId
        );

        verify(
                refreshTokenRepository
        ).revokeToken(
                tokenId
        );
    }
}


