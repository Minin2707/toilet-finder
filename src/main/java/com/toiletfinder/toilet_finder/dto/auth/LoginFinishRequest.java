package com.toiletfinder.toilet_finder.dto.auth;

import com.yubico.webauthn.data.AuthenticatorAssertionResponse;
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs;
import com.yubico.webauthn.data.PublicKeyCredential;
import lombok.Data;

@Data
public class LoginFinishRequest {

    private String username;

    private PublicKeyCredential<
                AuthenticatorAssertionResponse,
                ClientAssertionExtensionOutputs
                > credential;
}
