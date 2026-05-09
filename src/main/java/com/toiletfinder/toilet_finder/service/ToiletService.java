package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.CreateToiletRequest;
import com.toiletfinder.toilet_finder.dto.NearbyToiletResponse;
import com.toiletfinder.toilet_finder.enumStatus.ToiletStatus;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.repository.ApprovalRepository;
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

    public List<NearbyToiletResponse> findNearby(
            double lat,
            double lon,
            int radiusMeters,
            int limit
    ) {

        return toiletRepository.findNearby(
                lat,
                lon,
                radiusMeters,
                limit
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
}