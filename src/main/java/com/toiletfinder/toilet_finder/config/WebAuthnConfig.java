package com.toiletfinder.toilet_finder.config;

import com.toiletfinder.toilet_finder.security.CustomCredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class WebAuthnConfig {

    private final CustomCredentialRepository credentialRepository;

    @Value("${webauthn.rp-id}")
    private String rpId;

    @Value("${webauthn.origin}")
    private String origin;

    @Bean
    public RelyingParty relyingParty() {



        RelyingPartyIdentity identity =
                RelyingPartyIdentity.builder()
                        .id(rpId)
                        .name("Toilet Finder")
                        .build();

        return RelyingParty.builder()
                .identity(identity)
                .credentialRepository(credentialRepository)

                .origins(Set.of(origin))

                .appId(Optional.empty())
                .allowUntrustedAttestation(true)

                .build();
    }
}
