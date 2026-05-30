package com.toiletfinder.toilet_finder.dto.auth;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginFinishRequest {
    @NotBlank
    @Size(max = 100)
    private String username;

    @NotNull
    private PublicKeyCredential<
                AuthenticatorAssertionResponse,
                ClientAssertionExtensionOutputs
                > credential;
}
