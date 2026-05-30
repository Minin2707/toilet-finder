package com.toiletfinder.toilet_finder.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginStartRequest {
    @NotBlank
    @Size(max = 100)
    private String username;
}
