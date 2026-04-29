package com.toiletfinder.toilet_finder.controller;

import com.toiletfinder.toilet_finder.dto.CreateToiletRequest;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.service.ToiletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/toilets")
@RequiredArgsConstructor
public class ToiletController {

    private final ToiletService toiletService;

    @GetMapping("/nearby")
    public List<Toilet> findNearby(
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

    @PostMapping("/{toiletId}/approve")
    public String approve(
            @PathVariable UUID toiletId,
            @RequestParam UUID userId
    ) {
        toiletService.approve(toiletId, userId);

        return "Approval successful";
    }

    //TODO delete
}
