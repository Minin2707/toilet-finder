package com.toiletfinder.toilet_finder.security;


import com.toiletfinder.toilet_finder.model.User;
import com.toiletfinder.toilet_finder.repository.UserRepository;
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.AuthenticatorTransport;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import com.yubico.webauthn.data.exception.Base64UrlException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomCredentialRepository implements CredentialRepository {

    private final UserRepository userRepository;

    /**
     * login start:
     * find all credentials for username
     */
    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(
            String username
    ) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return Collections.emptySet();
        }

        try {
            return Set.of(
//                    PublicKeyCredentialDescriptor.builder()
//                            .id(
//                                    ByteArray.fromBase64Url(
//                                            user.getCredentialId()
//                                    )
//                            )
//                            .build()
                    PublicKeyCredentialDescriptor.builder()

                            .id(
                                    ByteArray.fromBase64Url(
                                            user.getCredentialId()
                                    )
                            )

                            .transports(Set.of(
                                    AuthenticatorTransport.INTERNAL
                            ))

                            .build()
            );
        } catch (Base64UrlException e) {
            return Collections.emptySet();
        }
    }

    /**
     * username -> userHandle
     */
    @Override
    public Optional<ByteArray> getUserHandleForUsername(
            String username
    ) {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(
                new ByteArray(
                        user.getId()
                                .toString()
                                .getBytes(StandardCharsets.UTF_8)
                )
        );
    }

    /**
     * userHandle -> username
     */
    @Override
    public Optional<String> getUsernameForUserHandle(
            ByteArray userHandle
    ) {
        String userId =
                new String(
                        userHandle.getBytes(),
                        StandardCharsets.UTF_8
                );

        User user =
                userRepository.findById(userId);

        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user.getUsername());
    }

    /**
     * login finish:
     * find exact credential
     */
    @Override
    public Optional<RegisteredCredential> lookup(
            ByteArray credentialId,
            ByteArray userHandle
    ) {
        User user =
                userRepository.findByCredentialId(
                        credentialId.getBase64Url()
                );

        if (user == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(
                    RegisteredCredential.builder()
                            .credentialId(credentialId)
                            .userHandle(userHandle)
                            .publicKeyCose(
                                    ByteArray.fromBase64Url(
                                            user.getPublicKey()
                                    )
                            )
                            .signatureCount(0)
                            .build()
            );

        } catch (Base64UrlException e) {
            return Optional.empty();
        }
    }

    /**
     * required by interface
     */
    @Override
    public Set<RegisteredCredential> lookupAll(
            ByteArray credentialId
    ) {
        User user =
                userRepository.findByCredentialId(
                        credentialId.getBase64Url()
                );

        if (user == null) {
            return Collections.emptySet();
        }

        try {
            RegisteredCredential credential =
                    RegisteredCredential.builder()
                            .credentialId(credentialId)
                            .userHandle(
                                    new ByteArray(
                                            user.getId()
                                                    .toString()
                                                    .getBytes(
                                                            StandardCharsets.UTF_8
                                                    )
                                    )
                            )
                            .publicKeyCose(
                                    ByteArray.fromBase64Url(
                                            user.getPublicKey()
                                    )
                            )
                            .signatureCount(0)
                            .build();

            return Set.of(credential);

        } catch (Base64UrlException e) {
            return Collections.emptySet();
        }
    }
}
