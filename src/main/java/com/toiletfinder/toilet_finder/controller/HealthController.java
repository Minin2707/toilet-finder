package com.toiletfinder.toilet_finder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {

        return Map.of(

                "status", "UP",

                "service", "toilet-finder",

                "version", "1.0.0"
        );
    }
}