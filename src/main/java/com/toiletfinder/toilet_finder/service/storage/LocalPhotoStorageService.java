package com.toiletfinder.toilet_finder.service.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

//@Service
public class LocalPhotoStorageService
        implements PhotoStorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

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

            Path uploadPath =
                    Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {

                Files.createDirectories(
                        uploadPath
                );
            }

            Path target =
                    uploadPath.resolve(filename);

            Files.copy(

                    file.getInputStream(),

                    target,

                    StandardCopyOption.REPLACE_EXISTING
            );

            return "/uploads/" + filename;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to save photo"
            );
        }
    }
}