package com.toiletfinder.toilet_finder.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toiletfinder.toilet_finder.dto.auth.AuthMeResponse;
import com.toiletfinder.toilet_finder.dto.auth.AuthTokensResponse;
import com.toiletfinder.toilet_finder.dto.auth.LoginFinishRequest;
import com.toiletfinder.toilet_finder.dto.auth.LoginStartRequest;
import com.toiletfinder.toilet_finder.dto.auth.RefreshTokenRequest;
import com.toiletfinder.toilet_finder.dto.auth.RegisterFinishRequest;
import com.toiletfinder.toilet_finder.dto.auth.RegisterStartRequest;
import com.toiletfinder.toilet_finder.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @PostMapping("/register/start")
    public Map<String, Object> startRegister(
            @Valid
            @RequestBody RegisterStartRequest request
    ) {

        var options = authService.startRegistration(request.getUsername());

        Map<String, Object> json =
                objectMapper.convertValue(
                        options,
                        new TypeReference<Map<String, Object>>() {}
                );

        json.remove("extensions");

        return json;
    }

    @PostMapping("/register/finish")
    public AuthTokensResponse finishRegister(
            @Valid
            @RequestBody RegisterFinishRequest request
    ) {
        return authService.finishRegistration(
                request
        );
    }

    @PostMapping("/login/start")
    public Map<String, Object> startLogin(
            @Valid
            @RequestBody LoginStartRequest request
    ) {

        var assertion = authService.startLogin(request.getUsername());

        Map<String, Object> json =
                objectMapper.convertValue(
                        assertion,
                        new TypeReference<Map<String, Object>>() {}
                );

        json.remove("extensions"); // как раньше

        return json;
    }

    @PostMapping("/login/finish")
    public AuthTokensResponse finishLogin(
            @Valid
            @RequestBody LoginFinishRequest request
    ) {
        return authService.finishLogin(request);
    }

    @PostMapping("/refresh")
    public AuthTokensResponse refresh(

            @Valid
            @RequestBody
            RefreshTokenRequest request
    ) {

        return authService.refreshTokens(
                request.refreshToken()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeResponse> me() {

        Authentication authentication =

                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        Object principal =
                authentication.getPrincipal();

        if ("anonymousUser".equals(
                principal.toString()
        )) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        UUID userId;

        if (principal instanceof UUID uuid) {

            userId = uuid;

        } else {

            userId = UUID.fromString(
                    principal.toString()
            );
        }

        if (!authService.isUserValid(userId)) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .build();
        }

        return ResponseEntity.ok(

                new AuthMeResponse(
                        userId,
                        true
                )
        );
    }
}
