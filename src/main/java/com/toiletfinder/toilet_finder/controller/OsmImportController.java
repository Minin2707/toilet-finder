package com.toiletfinder.toilet_finder.controller;

import com.toiletfinder.toilet_finder.service.OsmImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class OsmImportController {

    private final OsmImportService osmImportService;

    @PostMapping("/import-osm")
    public String importOsm() {

        osmImportService.importGeoJson();

        return "OSM import started";
    }
}
