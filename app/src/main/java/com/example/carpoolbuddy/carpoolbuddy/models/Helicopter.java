package com.example.carpoolbuddy.carpoolbuddy.models;

public class Helicopter extends Vehicle {
    private int maximumAltitude;
    private int maximumAirSpeed;

    public int getMaximumAirSpeed() {
        return maximumAirSpeed;
    }

    public int getMaximumAltitude() {
        return maximumAltitude;
    }

    public void setMaximumAirSpeed(int maximumAirSpeed) {
        this.maximumAirSpeed = maximumAirSpeed;
    }

    public void setMaximumAltitude(int maximumAltitude) {
        this.maximumAltitude = maximumAltitude;
    }
}
