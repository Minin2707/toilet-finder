package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.enumStatus.ToiletStatus;
import com.toiletfinder.toilet_finder.exception.ToiletAlreadyApprovedException;
import com.toiletfinder.toilet_finder.exception.UserAlreadyApprovedException;
import com.toiletfinder.toilet_finder.exception.UserAlreadyReportedException;
import com.toiletfinder.toilet_finder.repository.ApprovalRepository;
import com.toiletfinder.toilet_finder.repository.FeedbackRepository;
import com.toiletfinder.toilet_finder.repository.ToiletReportRepository;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

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
}