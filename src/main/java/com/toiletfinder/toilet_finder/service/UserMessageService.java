package com.toiletfinder.toilet_finder.service;

import com.toiletfinder.toilet_finder.dto.UserMessageRequest;
import com.toiletfinder.toilet_finder.model.UserMessage;
import com.toiletfinder.toilet_finder.repository.UserMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserMessageService {

    private final UserMessageRepository userMessageRepository;

    public void create(
            UserMessageRequest request,
            UUID userId
    ) {

        UserMessage userMessage =
                new UserMessage();

        userMessage.setId(
                UUID.randomUUID()
        );

        userMessage.setUserId(
                userId
        );

        userMessage.setType(
                request.type()
        );

        userMessage.setMessage(
                request.message()
        );

        userMessage.setCreatedAt(
                LocalDateTime.now()
        );

        userMessageRepository.save(
                userMessage
        );
    }
}
