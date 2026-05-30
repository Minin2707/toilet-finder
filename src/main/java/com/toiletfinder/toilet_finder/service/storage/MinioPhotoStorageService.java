package com.toiletfinder.toilet_finder.service.storage;

import com.toiletfinder.toilet_finder.exception.PhotoStorageException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
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

            log.error(
                    "Failed to upload photo. contentType={}, size={}",
                    file.getContentType(),
                    file.getSize(),
                    e
            );

            throw new PhotoStorageException(
                    "Failed to upload photo to storage",
                    e
            );
        }
    }

    @Override
    public InputStream load(
            String filename
    ) {

        try {

            return minioClient.getObject(

                    GetObjectArgs.builder()

                            .bucket(bucket)

                            .object(filename)

                            .build()
            );

        } catch (Exception e) {

            log.error(
                    "Failed to load photo={}",
                    filename,
                    e
            );

            throw new PhotoStorageException(
                    "Failed to load photo",
                    e
            );
        }
    }
}