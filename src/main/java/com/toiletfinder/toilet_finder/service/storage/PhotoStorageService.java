package com.toiletfinder.toilet_finder.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoStorageService {

    String save(
            MultipartFile file
    );
}