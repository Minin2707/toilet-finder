package com.toiletfinder.toilet_finder.service.storage;

import com.toiletfinder.toilet_finder.exception.PhotoStorageException;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
        log.info("PHOTO SAVE START");

        try {

            String filename =
                    UUID.randomUUID() + ".jpg";

            BufferedImage originalImage =
                    ImageIO.read(
                            file.getInputStream()
                    );

            log.info(
                    "IMAGE READ: width={}, height={}",
                    originalImage.getWidth(),
                    originalImage.getHeight()
            );

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            Thumbnails.of(originalImage)
                    .size(1920, 1920)
                    .outputQuality(0.8)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            log.info(
                    "THUMBNAIL CREATED"
            );

            byte[] compressedBytes =
                    outputStream.toByteArray();

            InputStream compressedInputStream =
                    new ByteArrayInputStream(
                            compressedBytes
                    );


            log.info(
                    "UPLOAD TO MINIO START"
            );

            minioClient.putObject(

                    PutObjectArgs.builder()

                            .bucket(bucket)

                            .object(filename)

                            .stream(
                                    compressedInputStream,
                                    compressedBytes.length,
                                    -1
                            )
                            .contentType(
                                    "image/jpeg"
                            )

                            .build()
            );

            log.info(
                    "UPLOAD TO MINIO FINISHED"
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