package com.toiletfinder.toilet_finder.controller;

import com.toiletfinder.toilet_finder.service.storage.PhotoStorageService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
public class PhotoProxyController {

    private final PhotoStorageService
            photoStorageService;

    @GetMapping("/photos/{filename}")
    public void getPhoto(

            @PathVariable String filename,

            HttpServletResponse response
    ) throws Exception {

        try (

                InputStream stream =
                        photoStorageService.load(
                                filename
                        )
        ) {

            if (filename.endsWith(".png")) {

                response.setContentType(
                        "image/png"
                );

            } else if (
                    filename.endsWith(".webp")
            ) {

                response.setContentType(
                        "image/webp"
                );

            } else {

                response.setContentType(
                        "image/jpeg"
                );
            }

            stream.transferTo(
                    response.getOutputStream()
            );

            response.flushBuffer();
        }
    }
}