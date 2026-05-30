package com.toiletfinder.toilet_finder.dto.auth;

import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterFinishRequest {
    @NotBlank
    @Size(max = 100)
    private String username;

    @NotNull
    private PublicKeyCredential<
            AuthenticatorAttestationResponse,
            ClientRegistrationExtensionOutputs
            > credential;
}
