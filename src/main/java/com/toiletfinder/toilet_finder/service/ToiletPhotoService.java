package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.ToiletPhotoResponse;
import com.toiletfinder.toilet_finder.enumStatus.PhotoStatus;
import com.toiletfinder.toilet_finder.enumStatus.ToiletStatus;
import com.toiletfinder.toilet_finder.model.ToiletPhoto;
import com.toiletfinder.toilet_finder.repository.ToiletPhotoRepository;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import com.toiletfinder.toilet_finder.service.storage.PhotoStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToiletPhotoService {

    private final ToiletRepository toiletRepository;

    private final ToiletPhotoRepository toiletPhotoRepository;

    private final PhotoStorageService photoStorageService;

    @Transactional
    public void uploadPhoto(

            UUID toiletId,

            UUID userId,

            MultipartFile file
    ) {
        System.out.println("CHECKING STATUS");

        String toiletStatus =
                toiletRepository.findStatusById(
                        toiletId
                );

        if (!ToiletStatus.APPROVED.name()
                .equals(toiletStatus)) {

            throw new RuntimeException(
                    "Photos allowed only for approved toilets"
            );
        }

        System.out.println("STATUS OK");

        int activePhotos =
                toiletPhotoRepository
                        .countActivePhotos(
                                toiletId
                        );

        System.out.println("ACTIVE PHOTOS COUNT = " + activePhotos);

        if (activePhotos >= 2) {

            throw new RuntimeException(
                    "Maximum 2 photos allowed"
            );
        }

        System.out.println("STARTING FILE SAVE");

        String photoUrl =
                photoStorageService.save(
                        file
                );

        System.out.println("FILE SAVED");

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

        System.out.println("STARTING DB SAVE");

        toiletPhotoRepository.save(
                photo
        );

        System.out.println("DB SAVE FINISHED");
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