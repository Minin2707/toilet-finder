package com.toiletfinder.toilet_finder.controller;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
public class PhotoProxyController {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @GetMapping("/photos/{filename}")
    public void getPhoto(

            @PathVariable String filename,

            HttpServletResponse response
    ) {

        try (

                InputStream stream =

                        minioClient.getObject(

                                GetObjectArgs.builder()

                                        .bucket(bucket)

                                        .object(filename)

                                        .build()
                        )
        ) {

            byte[] bytes =
                    stream.readAllBytes();

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

            response.setContentLength(
                    bytes.length
            );

            response.getOutputStream()
                    .write(bytes);

            response.getOutputStream()
                    .flush();

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(404);
        }
    }
}