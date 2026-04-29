package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.CreateToiletRequest;
import com.toiletfinder.toilet_finder.model.Toilet;
import com.toiletfinder.toilet_finder.repository.ApprovalRepository;
import com.toiletfinder.toilet_finder.repository.ToiletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ToiletService {

    private final ToiletRepository toiletRepository;
    private final ApprovalRepository approvalRepository;

    public List<Toilet> findNearby(double lat, double lon) {
        return toiletRepository.findNearby(lat, lon);
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

    public void approve(UUID toiletId, UUID userId) {

        String currentStatus = toiletRepository.findStatusById(toiletId);

        if ("APPROVED".equals(currentStatus)) {
            throw new RuntimeException("Toilet already approved");
        }

        boolean alreadyApproved =
                approvalRepository.alreadyApproved(toiletId, userId);

        if (alreadyApproved) {
            throw new RuntimeException("User already approved this toilet");
        }

        approvalRepository.save(toiletId, userId);

        int approvalsCount =
                approvalRepository.countApprovals(toiletId);

        if (approvalsCount >= 2) {
            toiletRepository.updateStatus(
                    toiletId,
                    "APPROVED"
            );
        }
    }
}