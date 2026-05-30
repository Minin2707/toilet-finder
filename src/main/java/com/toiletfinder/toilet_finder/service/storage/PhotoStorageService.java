package com.toiletfinder.toilet_finder.service.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface PhotoStorageService {

    String save(
            MultipartFile file
    );

    InputStream load(
            String filename
    );
}