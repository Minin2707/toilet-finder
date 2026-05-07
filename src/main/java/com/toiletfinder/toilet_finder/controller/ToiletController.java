package com.toiletfinder.toilet_finder.controller;

import com.toiletfinder.toilet_finder.dto.CreateToiletRequest;
import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.service.ToiletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/toilets")
@RequiredArgsConstructor
public class ToiletController {

    private final ToiletService toiletService;

    @GetMapping("/nearby")
    public List<NearbyToiletResponse> nearby(
            @RequestParam double lat,
            @RequestParam double lon
    ) {

        return toiletService.findNearby(lat, lon);
    }

    @PostMapping
    public Toilet createToilet(
            @RequestBody @Valid CreateToiletRequest request
    ) {
        return toiletService.create(request);
    }

    @PostMapping("/{id}/approve")
    public void approve(@PathVariable UUID id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        UUID userId = (UUID) authentication.getPrincipal();

        toiletService.approve(id, userId);
        System.out.println("AUTH = " + authentication);
        System.out.println("USER = " + userId);
    }

    //TODO delete
}
