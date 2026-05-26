package com.toiletfinder.toilet_finder.service.storage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioPhotoStorageService
        implements PhotoStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String save(
            MultipartFile file
    ) {

        try {

            String extension =
                    StringUtils.getFilenameExtension(
                            file.getOriginalFilename()
                    );

            String filename =
                    UUID.randomUUID() + "." + extension;

            InputStream inputStream =
                    file.getInputStream();

            minioClient.putObject(

                    PutObjectArgs.builder()

                            .bucket(bucket)

                            .object(filename)

                            .stream(
                                    inputStream,
                                    file.getSize(),
                                    -1
                            )

                            .contentType(
                                    file.getContentType()
                            )

                            .build()
            );

            return "/photos/" + filename;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to upload photo to MinIO",
                    e
            );
        }
    }
}