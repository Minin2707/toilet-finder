package com.toiletfinder.toilet_finder.controller;

import com.toiletfinder.toilet_finder.dto.ToiletPhotoResponse;
import com.toiletfinder.toilet_finder.service.ToiletPhotoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/toilet-photos")
public class ToiletPhotoController {

    private final ToiletPhotoService
            toiletPhotoService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping(
            value = "/{toiletId}",
            consumes = "multipart/form-data"
    )
    public void uploadPhoto(

            @PathVariable UUID toiletId,

            @RequestParam("photo")
            MultipartFile photo
    ) {


        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        UUID userId =
                (UUID) authentication
                        .getPrincipal();

        toiletPhotoService.uploadPhoto(

                toiletId,

                userId,

                photo
        );

    }

    @GetMapping("/{toiletId}")
    public List<ToiletPhotoResponse> getPhotos(
            @PathVariable UUID toiletId
    ) {

        return toiletPhotoService
                .getPhotos(
                        toiletId
                );
    }
}