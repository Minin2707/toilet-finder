package com.toiletfinder.toilet_finder.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class CreateToiletRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String address;
}