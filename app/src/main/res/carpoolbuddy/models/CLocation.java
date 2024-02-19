package com.example.carpoolbuddy.models;


public class CLocation {
    private double latitude;
    private double longitude;
    private String address;
    private String placeId;

    public CLocation( String address, String placeId) {
        this.address = address;
        this.placeId = placeId;
    }
    public CLocation() {

    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceId() {
        return placeId;
    }
}