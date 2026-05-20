package com.toiletfinder.toilet_finder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toiletfinder.toilet_finder.dto.auth.LoginFinishRequest;
import com.toiletfinder.toilet_finder.dto.auth.RegisterFinishRequest;
import com.toiletfinder.toilet_finder.model.AuthChallenge;
import com.toiletfinder.toilet_finder.model.User;
import com.toiletfinder.toilet_finder.repository.AuthChallengeRepository;
import com.toiletfinder.toilet_finder.repository.UserRepository;
import com.toiletfinder.toilet_finder.security.JwtService;
import com.yubico.webauthn.AssertionRequest;
import com.yubico.webauthn.AssertionResult;
import com.yubico.webauthn.FinishAssertionOptions;
import com.yubico.webauthn.RegistrationResult;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.StartAssertionOptions;
import com.yubico.webauthn.StartRegistrationOptions;
import com.yubico.webauthn.data.*;
import com.yubico.webauthn.exception.RegistrationFailedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RelyingParty relyingParty;
    private final AuthChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private static final Logger log =
            LoggerFactory.getLogger(
                    AuthService.class
            );

    public PublicKeyCredentialCreationOptions startRegistration(
            String username
    ) {

        log.info(
                "Starting registration for username={}",
                username
        );

        UserIdentity userIdentity =
                UserIdentity.builder()
                        .name(username)
                        .displayName(username)
                        .id(
                                new ByteArray(
                                        UUID.randomUUID()
                                                .toString()
                                                .getBytes()
                                )
                        )
                        .build();

        PublicKeyCredentialCreationOptions options =
                relyingParty.startRegistration(
                        StartRegistrationOptions.builder()
                                .user(userIdentity)
                                .build()
                );

        options = PublicKeyCredentialCreationOptions.builder()
                .rp(options.getRp())
                .user(options.getUser())
                .challenge(options.getChallenge())
                .pubKeyCredParams(options.getPubKeyCredParams())
                .timeout(options.getTimeout())
                .excludeCredentials(options.getExcludeCredentials())
                .authenticatorSelection(options.getAuthenticatorSelection())
                .attestation(options.getAttestation())

                // 🔥 КРИТИЧЕСКАЯ СТРОКА
                .extensions(RegistrationExtensionInputs.builder().build())

                .build();

        String optionsJson;

        try {
            optionsJson = objectMapper.writeValueAsString(options);
        } catch (Exception e) {
            log.error(
                    "Failed to serialize WebAuthn options={}",
                    username,
                    e
            );
            throw new RuntimeException(
                    "Failed to serialize WebAuthn options",
                    e
            );
        }

        challengeRepository.deleteByUsernameAndType(
                username,
                "REGISTER"
        );

        AuthChallenge challenge = new AuthChallenge();

        challenge.setId(UUID.randomUUID());
        challenge.setUsername(username);

        challenge.setChallenge(
                options.getChallenge().getBase64Url()
        );

        challenge.setChallengeType("REGISTER");

        challenge.setCreatedAt(LocalDateTime.now());

        challenge.setExpiresAt(
                LocalDateTime.now().plusMinutes(5)
        );

        challenge.setOptionsJson(optionsJson);

        challengeRepository.save(challenge);

        log.info(
                "Registration challenge saved for username={}",
                username
        );

        return options;
    }

    public String finishRegistration(RegisterFinishRequest request) {

        log.info(
                "Finishing registration for username={}",
                request.getUsername()
        );

        // 1. Получаем challenge из БД
        AuthChallenge challenge =
                challengeRepository.findValidChallenge(
                        request.getUsername(),
                        "REGISTER"
                );

        if (challenge == null) {

            log.info("Challenge expired or not found");

            throw new RuntimeException("Challenge expired or not found");
        }

        // 2. Восстанавливаем options из JSON
        PublicKeyCredentialCreationOptions options;

        try {
            options = objectMapper.readValue(
                    challenge.getOptionsJson(),
                    PublicKeyCredentialCreationOptions.class
            );
        } catch (Exception e) {

            log.info("Failed to deserialize options", e);

            throw new RuntimeException("Failed to deserialize options", e);
        }

        // 3. Верификация WebAuthn
        RegistrationResult result;

        try {
            result = relyingParty.finishRegistration(
                    com.yubico.webauthn.FinishRegistrationOptions
                            .builder()
                            .request(options)
                            .response(request.getCredential())
                            .build()
            );
        } catch (RegistrationFailedException e) {

            log.info("Passkey registration failed", e);

            throw new RuntimeException("Passkey registration failed", e);
        }

        // 4. Сохраняем пользователя
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());

        user.setCredentialId(
                result.getKeyId()
                        .getId()
                        .getBase64Url()
        );

        user.setPublicKey(
                result.getPublicKeyCose()
                        .getBase64Url()
        );

        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        log.info(
                "Registration successful for username={}",
                request.getUsername()
        );

        // 5. Удаляем challenge (очень важно)
        challengeRepository.deleteByUsernameAndType(
                request.getUsername(),
                "REGISTER"
        );

        // 6. Генерируем JWT
        return jwtService.generateToken(user.getId());
    }

    public AssertionRequest startLogin(String username) {

        log.info(
                "Starting login for username={}",
                username
        );

        AssertionRequest request =
                relyingParty.startAssertion(
                        StartAssertionOptions.builder()
                                .username(username)
                                .build()
                );

        challengeRepository.deleteByUsernameAndType(
                username,
                "LOGIN"
        );

        // сохраняем в БД (как делали для register)
        AuthChallenge challenge = new AuthChallenge();

        challenge.setId(UUID.randomUUID());
        challenge.setUsername(username);
        challenge.setChallenge(
                request.getPublicKeyCredentialRequestOptions()
                        .getChallenge()
                        .getBase64Url()
        );
        challenge.setChallengeType("LOGIN");
        challenge.setCreatedAt(LocalDateTime.now());
        challenge.setExpiresAt(LocalDateTime.now().plusMinutes(5));

        try {
            challenge.setOptionsJson(
                    objectMapper.writeValueAsString(request)
            );
        } catch (Exception e) {

            log.info("Serialize failed", e);

            throw new RuntimeException("Serialize failed", e);
        }

        challengeRepository.save(challenge);

        log.info(
                "Login challenge saved for username={}",
                username
        );

        return request;
    }

    public String finishLogin(LoginFinishRequest request) {

        log.info(
                "Finishing login for username={}",
                request.getUsername()
        );

        AuthChallenge challenge =
                challengeRepository.findValidChallenge(
                        request.getUsername(),
                        "LOGIN"
                );

        if (challenge == null) {

            log.info("Challenge expired");

            throw new RuntimeException("Challenge expired");
        }

        AssertionRequest assertionRequest;

        try {
            assertionRequest = objectMapper.readValue(
                    challenge.getOptionsJson(),
                    AssertionRequest.class
            );
        } catch (Exception e) {

            log.info("Deserialize failed", e);

            throw new RuntimeException("Deserialize failed", e);
        }

        AssertionResult result;

        try {
            result = relyingParty.finishAssertion(
                    FinishAssertionOptions.builder()
                            .request(assertionRequest)
                            .response(request.getCredential())
                            .build()
            );
        } catch (Exception e) {

            log.info("Login failed", e);

            throw new RuntimeException("Login failed", e);
        }

        if (!result.isSuccess()) {

            log.info("Invalid passkey");

            throw new RuntimeException("Invalid passkey");
        }

        log.info(
                "Login successful for username={}",
                request.getUsername()
        );

        User user = userRepository.findByUsername(request.getUsername());

        challengeRepository.deleteByUsernameAndType(
                request.getUsername(),
                "LOGIN"
        );

        return jwtService.generateToken(user.getId());
    }

    public boolean isUserValid(UUID userId) {

        return userRepository.existsById(userId);
    }
}
