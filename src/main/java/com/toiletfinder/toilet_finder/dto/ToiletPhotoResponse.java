package com.toiletfinder.toilet_finder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class ToiletPhotoResponse {

    private UUID id;

    private String photoUrl;
}