package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.enumStatus.ToiletStatus;
import com.toiletfinder.toilet_finder.exception.RateLimitExceededException;
import com.toiletfinder.toilet_finder.exception.ToiletAlreadyApprovedException;
import com.toiletfinder.toilet_finder.exception.UserAlreadyApprovedException;
import com.toiletfinder.toilet_finder.exception.UserAlreadyReportedException;
import com.toiletfinder.toilet_finder.repository.ApprovalRepository;
import com.toiletfinder.toilet_finder.repository.FeedbackRepository;
import com.toiletfinder.toilet_finder.repository.ToiletReportRepository;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ToiletServiceTest {

    @Mock
    private ToiletRepository toiletRepository;

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private ToiletReportRepository toiletReportRepository;

    @Mock
    private RateLimitService rateLimitService;

    @InjectMocks
    private ToiletService toiletService;

    @BeforeEach
    void setup() {

        lenient().when(
                rateLimitService.resolveApproveBucket(
                        any()
                )
        ).thenReturn(
                unlimitedBucket()
        );

        lenient().when(
                rateLimitService.resolveReportBucket(
                        any()
                )
        ).thenReturn(
                unlimitedBucket()
        );

        lenient().when(
                rateLimitService.resolveFeedbackBucket(
                        any()
                )
        ).thenReturn(
                unlimitedBucket()
        );
    }

    private Bucket unlimitedBucket() {

        return Bucket.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(1000)
                                .refillGreedy(
                                        1000,
                                        Duration.ofDays(1)
                                )
                                .build()
                )
                .build();
    }

    @Test
    void shouldRemainPendingAfterOneApproval() {

        UUID toiletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                toiletRepository.findStatusById(
                        toiletId
                )
        ).thenReturn(
                ToiletStatus.PENDING.name()
        );

        when(
                approvalRepository.countApprovals(
                        toiletId
                )
        ).thenReturn(1);

        toiletService.approve(
                toiletId,
                userId
        );

        verify(
                approvalRepository
        ).save(
                toiletId,
                userId
        );

        verify(
                toiletRepository,
                never()
        ).updateStatus(
                any(),
                any()
        );
    }

    @Test
    void shouldApproveToiletAfterThreeApprovals() {

        UUID toiletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                toiletRepository.findStatusById(
                        toiletId
                )
        ).thenReturn(
                ToiletStatus.PENDING.name()
        );

        when(
                approvalRepository.countApprovals(
                        toiletId
                )
        ).thenReturn(3);

        toiletService.approve(
                toiletId,
                userId
        );

        verify(
                toiletRepository
        ).updateStatus(
                toiletId,
                ToiletStatus.APPROVED.name()
        );
    }

    @Test
    void shouldThrowWhenToiletAlreadyApproved() {

        UUID toiletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                toiletRepository.findStatusById(
                        toiletId
                )
        ).thenReturn(
                ToiletStatus.APPROVED.name()
        );

        assertThrows(

                ToiletAlreadyApprovedException.class,

                () -> toiletService.approve(
                        toiletId,
                        userId
                )
        );

        verify(
                approvalRepository,
                never()
        ).save(
                any(),
                any()
        );
    }

    @Test
    void shouldThrowWhenUserAlreadyApproved() {

        UUID toiletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                toiletRepository.findStatusById(
                        toiletId
                )
        ).thenReturn(
                ToiletStatus.PENDING.name()
        );

        doThrow(
                DataIntegrityViolationException.class
        ).when(
                approvalRepository
        ).save(
                toiletId,
                userId
        );

        assertThrows(

                UserAlreadyApprovedException.class,

                () -> toiletService.approve(
                        toiletId,
                        userId
                )
        );
    }

    @Test
    void shouldMoveToNeedsRevalidationAfterThreeReports() {

        UUID toiletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(
                toiletReportRepository.countReports(
                        toiletId
                )
        ).thenReturn(3);

        toiletService.reportToilet(
                toiletId,
                userId
        );

        verify(
                toiletRepository
        ).updateStatus(

                toiletId,

                ToiletStatus
                        .NEEDS_REVALIDATION
                        .name()
        );

        verify(
                toiletRepository
        ).resetRevalidationConfirmations(
                toiletId
        );
    }

    @Test
    void shouldThrowWhenUserAlreadyReported() {

        UUID toiletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doThrow(
                DataIntegrityViolationException.class
        ).when(
                toiletReportRepository
        ).save(
                toiletId,
                userId
        );

        assertThrows(

                UserAlreadyReportedException.class,

                () -> toiletService.reportToilet(
                        toiletId,
                        userId
                )
        );
    }

    @Test
    void shouldThrowWhenApproveRateLimitExceeded() {

        UUID toiletId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Bucket bucket =
                Bucket.builder()
                        .addLimit(
                                Bandwidth.builder()
                                        .capacity(1)
                                        .refillGreedy(
                                                1,
                                                Duration.ofDays(1)
                                        )
                                        .build()
                        )
                        .build();

        bucket.tryConsume(1);

        when(
                rateLimitService.resolveApproveBucket(
                        userId
                )
        ).thenReturn(bucket);

        assertThrows(

                RateLimitExceededException.class,

                () -> toiletService.approve(
                        toiletId,
                        userId
                )
        );
    }
}