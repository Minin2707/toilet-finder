package com.toiletfinder.toilet_finder.controller;

import com.toiletfinder.toilet_finder.dto.UserMessageRequest;
import com.toiletfinder.toilet_finder.service.UserMessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user-messages")
@RequiredArgsConstructor
public class UserMessageController {

    private final UserMessageService userMessageService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public void create(
            @RequestBody @Valid UserMessageRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        UUID userId =
                (UUID) authentication.getPrincipal();

        userMessageService.create(
                request,
                userId
        );
    }
}
