package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.CreateToiletRequest;
import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import com.toiletfinder.toilet_finder.enumStatus.ToiletStatus;
import com.toiletfinder.toilet_finder.exception.UserAlreadyApprovedException;
import com.toiletfinder.toilet_finder.exception.UserAlreadyReportedException;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.repository.ApprovalRepository;
import com.toiletfinder.toilet_finder.repository.FeedbackRepository;
import com.toiletfinder.toilet_finder.repository.ToiletReportRepository;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import com.toiletfinder.toilet_finder.exception.ToiletAlreadyApprovedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final ToiletReportRepository toiletReportRepository;
    private static final Logger log =

            LoggerFactory.getLogger(
                    ToiletService.class
            );

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

        log.info(

                "Toilet created: id={}, title={}",

                toilet.getId(),

                toilet.getTitle()
        );

        return toilet;
    }

    @Transactional
    public void approve(
            UUID toiletId,
            UUID userId
            ) {

        String currentStatus =
                toiletRepository.findStatusById(
                        toiletId
                        );

        if (ToiletStatus.APPROVED.name()
                .equals(currentStatus)) {

            throw new ToiletAlreadyApprovedException();
        }

        try {

            approvalRepository.save(
                    toiletId,
                    userId
                    );

        } catch (
                DataIntegrityViolationException e
        ) {

            throw new UserAlreadyApprovedException();
        }

        int approvalsCount =
                approvalRepository.countApprovals(
                        toiletId
                        );

        if (approvalsCount >= 1) {

            toiletRepository.updateStatus(
                    toiletId,
                    "APPROVED"
                    );
        }

        log.info(

                "Toilet approved: toiletId={}, approvedByUserId={}",

                toiletId,

                userId
        );
    }

    @Transactional
    public void reportToilet(

            UUID toiletId,

            UUID userId
    ) {

        try {

            toiletReportRepository.save(

                    toiletId,

                    userId
            );

        } catch (
                DataIntegrityViolationException e
        ) {

            throw new UserAlreadyReportedException();
        }

        int reports =
                toiletReportRepository
                        .countReports(
                                toiletId
                        );

        if (reports >= 1) {

            toiletRepository.updateStatus(

                    toiletId,

                    ToiletStatus
                            .NEEDS_REVALIDATION
                            .name()
            );

            toiletRepository
                    .resetRevalidationConfirmations(
                            toiletId
                    );

            log.info(

                    "Toilet reported and moved to NEEDS_REVALIDATION: toiletId={}, reportedByUserId={}",

                    toiletId,

                    userId
            );
        }
    }

    @Transactional
    public void confirm(UUID toiletId) {

        String currentStatus =
                toiletRepository.findStatusById(
                        toiletId
                );

        if (ToiletStatus.NEEDS_REVALIDATION.name()
                .equals(currentStatus)) {

            toiletRepository
                    .incrementRevalidationConfirmations(
                            toiletId
                    );

            int confirmations =
                    toiletRepository
                            .getRevalidationConfirmations(
                                    toiletId
                            );

            if (confirmations >= 1) {

                toiletRepository.updateStatus(
                        toiletId,
                        ToiletStatus.APPROVED.name()
                );

                toiletRepository
                        .resetRevalidationConfirmations(
                                toiletId
                        );

                toiletRepository
                        .resetReportCount(
                                toiletId
                        );

                log.info(

                        "Toilet revalidated and approved again: toiletId={}",

                        toiletId
                );
            }

            return;
        }

        toiletRepository.confirm(
                toiletId
        );
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