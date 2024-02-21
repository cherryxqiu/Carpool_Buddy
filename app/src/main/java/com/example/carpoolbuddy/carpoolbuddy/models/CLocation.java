package com.example.carpoolbuddy.carpoolbuddy.models;

public class CLocation {
    private double latitude;
    private double longitude;
    private String address;
    private String placeIdentifier;

    public CLocation(String address, String placeIdentifier) {
        this.address = address;
        this.placeIdentifier = placeIdentifier;
    }

    public CLocation() {}

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceIdentifier() {
        return placeIdentifier;
    }
}
