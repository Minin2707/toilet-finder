package com.toiletfinder.toilet_finder.controller;

import com.toiletfinder.toilet_finder.dto.CreateToiletRequest;
import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.service.ToiletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Toilets")
@RestController
@RequestMapping("/toilets")
@RequiredArgsConstructor
public class ToiletController {

    private final ToiletService toiletService;

    @Operation(
            summary = "Find nearby toilets",
            description = "Returns nearby approved toilets sorted by distance"
    )
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/nearby")
    public List<NearbyToiletResponse> nearby(

            @RequestParam @Min(-90) @Max(90) double lat,
            @RequestParam @Min(-180) @Max(180) double lon,
            @RequestParam(defaultValue = "1000")
            int radiusMeters,

            @RequestParam(defaultValue = "20")
            int limit
    ) {

        return toiletService.findNearby(
                lat,
                lon,
                radiusMeters,
                limit
        );
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public Toilet createToilet(
            @RequestBody @Valid CreateToiletRequest request
    ) {
        return toiletService.create(request);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/approve")
    public void approve(@PathVariable UUID id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UUID userId = (UUID) authentication.getPrincipal();

        toiletService.approve(id, userId);
        System.out.println("AUTH = " + authentication);
        System.out.println("USER = " + userId);
    }
}
