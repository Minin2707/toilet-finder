package com.toiletfinder.toilet_finder.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterStartRequest {

    @NotBlank(
            message = "Username is required"
    )
    @Size(
            max = 100,
            message = "Username too long"
    )
    private String username;
}
