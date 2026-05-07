package com.toiletfinder.toilet_finder.dto.auth;

import com.yubico.webauthn.data.AuthenticatorAttestationResponse;
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import lombok.Data;

@Data
public class RegisterFinishRequest {

    private String username;

    private PublicKeyCredential<
            AuthenticatorAttestationResponse,
            ClientRegistrationExtensionOutputs
            > credential;
}
