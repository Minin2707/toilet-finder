package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.exception.InvalidRefreshTokenException;
import com.toiletfinder.toilet_finder.model.RefreshToken;
import com.toiletfinder.toilet_finder.repository.RefreshTokenRepository;
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

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldCreateRefreshToken() {

        UUID userId = UUID.randomUUID();

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(
                        userId
                );

        assertNotNull(
                refreshToken.getId()
        );

        assertEquals(
                userId,
                refreshToken.getUserId()
        );

        assertFalse(
                refreshToken.isRevoked()
        );

        assertNotNull(
                refreshToken.getToken()
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
                refreshTokenRepository.findByToken(
                        token
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
                refreshTokenRepository.findByToken(
                        "token"
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
                refreshTokenRepository.findByToken(
                        "token"
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
                refreshTokenRepository.findByToken(
                        "token"
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


