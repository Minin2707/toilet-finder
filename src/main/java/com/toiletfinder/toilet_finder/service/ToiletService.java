package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.CreateToiletRequest;
import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import com.toiletfinder.toilet_finder.enumStatus.ToiletStatus;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.repository.ApprovalRepository;
import com.toiletfinder.toilet_finder.repository.FeedbackRepository;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToiletService {

    private final ToiletRepository toiletRepository;
    private final ApprovalRepository approvalRepository;
    private final FeedbackRepository feedbackRepository;

    public List<NearbyToiletResponse> findNearby(

            double lat,

            double lon,

            int radiusMeters,

            int limit,

            Boolean approvedOnly,

            Boolean accessibleOnly,

            String accessType
    ) {

        return toiletRepository.findNearby(

                lat,

                lon,

                radiusMeters,

                limit,

                approvedOnly,

                accessibleOnly,

                accessType
        );
    }
    public Toilet create(CreateToiletRequest request) {
        Toilet toilet = new Toilet();

        toilet.setId(UUID.randomUUID());
        toilet.setTitle(request.getTitle());
        toilet.setDescription(request.getDescription());
        toilet.setLatitude(request.getLatitude());
        toilet.setLongitude(request.getLongitude());
        toilet.setAddress(request.getAddress());
        toilet.setAccessType(
                request.getAccessType()
        );

        toilet.setWheelchairAccessible(
                request.getWheelchairAccessible()
        );

        toilet.setStatus("PENDING");
        toilet.setCreatedAt(LocalDateTime.now());

        toiletRepository.save(toilet);

        return toilet;
    }

    @Transactional
    public void approve(UUID toiletId, UUID userId) {

        String currentStatus = toiletRepository.findStatusById(toiletId);

        if (ToiletStatus.APPROVED.name().equals(currentStatus)) {
            throw new RuntimeException("Toilet already approved");
        }

        try {
            approvalRepository.save(toiletId, userId);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("User already approved this toilet");
        }

        int approvalsCount = approvalRepository.countApprovals(toiletId);

        if (approvalsCount >= 2) {
            toiletRepository.updateStatus(toiletId, "APPROVED");
        }
    }

    @Transactional
    public void reportToilet(UUID toiletId) {

        toiletRepository.incrementReportCount(
                toiletId
        );

        Integer reports =
                toiletRepository.getReportCount(
                        toiletId
                );

        if (reports >= 1) {

            toiletRepository.updateStatus(
                    toiletId,
                    ToiletStatus.HIDDEN.name()
            );
        }
    }

    @Transactional
    public void confirm(UUID toiletId) {

        toiletRepository.confirm(toiletId);
    }

    @Transactional
    public void leaveFeedback(

            UUID toiletId,

            UUID userId,

            String feedbackType
    ) {

        boolean alreadyExists =

                feedbackRepository.exists(

                        toiletId,

                        userId,

                        feedbackType
                );

        if (alreadyExists) {

            feedbackRepository.delete(

                    toiletId,

                    userId,

                    feedbackType
            );

            return;
        }

        feedbackRepository.save(

                toiletId,

                userId,

                feedbackType
        );
    }
}