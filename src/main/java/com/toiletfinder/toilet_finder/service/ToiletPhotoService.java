package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.ToiletPhotoResponse;
import com.toiletfinder.toilet_finder.enumStatus.PhotoStatus;
import com.toiletfinder.toilet_finder.enumStatus.ToiletStatus;
import com.toiletfinder.toilet_finder.exception.InvalidPhotoException;
import com.toiletfinder.toilet_finder.exception.PhotoLimitExceededException;
import com.toiletfinder.toilet_finder.exception.PhotosAllowedOnlyForApprovedToiletsException;
import com.toiletfinder.toilet_finder.model.ToiletPhoto;
import com.toiletfinder.toilet_finder.repository.ToiletPhotoRepository;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import com.toiletfinder.toilet_finder.service.storage.PhotoStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToiletPhotoService {

    private final ToiletRepository toiletRepository;

    private final ToiletPhotoRepository toiletPhotoRepository;

    private final PhotoStorageService photoStorageService;
    private static final Logger log =

            LoggerFactory.getLogger(
                    ToiletPhotoService.class
            );


    private void validatePhoto(
            MultipartFile file
    ) {

        if (file.isEmpty()) {

            throw new InvalidPhotoException(
                    "File is empty"
            );
        }

        String contentType =
                file.getContentType();

        if (!List.of(

                "image/jpeg",
                "image/png",
                "image/webp"

        ).contains(contentType)) {

            throw new InvalidPhotoException(
                    "Unsupported file type"
            );
        }

        if (file.getSize() >
                10 * 1024 * 1024) {

            throw new InvalidPhotoException(
                    "File too large"
            );
        }
    }

    @Transactional
    public void uploadPhoto(

            UUID toiletId,

            UUID userId,

            MultipartFile file
    ) {

        validatePhoto(file);

        String toiletStatus =
                toiletRepository.findStatusById(
                        toiletId
                );

        if (!ToiletStatus.APPROVED.name()
                .equals(toiletStatus)) {

            throw new
                    PhotosAllowedOnlyForApprovedToiletsException();
        }


        int activePhotos =
                toiletPhotoRepository
                        .countActivePhotos(
                                toiletId
                        );


        if (activePhotos >= 2) {

            throw new
                    PhotoLimitExceededException();
        }


        String photoUrl =
                photoStorageService.save(
                        file
                );


        ToiletPhoto photo =
                new ToiletPhoto();

        photo.setId(
                UUID.randomUUID()
        );

        photo.setToiletId(
                toiletId
        );

        photo.setUploadedByUserId(
                userId
        );

        photo.setPhotoUrl(
                photoUrl
        );

        photo.setReportCount(0);

        photo.setStatus(
                PhotoStatus.ACTIVE.name()
        );

        photo.setCreatedAt(
                LocalDateTime.now()
        );


        toiletPhotoRepository.save(
                photo
        );

        log.info(

                "Photo uploaded: toiletId={}, userId={}, photoUrl={}",

                toiletId,

                userId,

                photoUrl
        );
    }

    public List<ToiletPhotoResponse> getPhotos(
            UUID toiletId
    ) {

        return toiletPhotoRepository
                .findActiveByToiletId(
                        toiletId
                );
    }
}