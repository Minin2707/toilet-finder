package com.toiletfinder.toilet_finder.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Toilet {

    private UUID id;
    private String title;
    private String description;
    private double latitude;
    private double longitude;
    private String address;
    private String status;
    private LocalDateTime createdAt;
    private String accessType;
    private Boolean wheelchairAccessible;
    private Integer reportCount = 0;

    public Integer getReportCount() {
        return reportCount;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }

    public Toilet() {
    }

    public Toilet(UUID id, String title, String description, double latitude, double longitude, String address, String status, LocalDateTime createdAt, String accessType, Boolean wheelchairAccessible) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.accessType = accessType;
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAccessType() {
        return accessType;
    }

    public Boolean getWheelchairAccessible() {
        return wheelchairAccessible;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public void setWheelchairAccessible(Boolean wheelchairAccessible) {
        this.wheelchairAccessible = wheelchairAccessible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
