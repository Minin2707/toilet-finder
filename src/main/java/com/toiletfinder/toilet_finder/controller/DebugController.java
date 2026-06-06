package com.toiletfinder.toilet_finder.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/debug")
@Slf4j
public class DebugController {

    @PostMapping("/test")
    public ResponseEntity<String> test() {

        log.info("TEST POST RECEIVED");

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/multipart")
    public ResponseEntity<String> multipart(
            @RequestParam("photo") MultipartFile photo
    ) {

        log.info("MULTIPART RECEIVED");
        log.info("NAME={}", photo.getOriginalFilename());
        log.info("SIZE={}", photo.getSize());

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/multipart-text")
    public ResponseEntity<String> multipartText(
            @RequestParam("test") String test
    ) {

        log.info("MULTIPART TEXT RECEIVED");
        log.info("VALUE={}", test);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/multipart-bytes")
    public ResponseEntity<String> multipartBytes(
            @RequestPart("photo") MultipartFile photo
    ) throws IOException {

        log.info("MULTIPART BYTES RECEIVED");
        log.info("NAME={}", photo.getOriginalFilename());
        log.info("SIZE={}", photo.getSize());

        byte[] bytes = photo.getBytes();

        log.info("BYTES READ={}", bytes.length);

        return ResponseEntity.ok("OK");
    }
}