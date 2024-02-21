package com.example.carpoolbuddy.carpoolbuddy.models;

public class Bike extends Vehicle {
    private int bikeWeight;
    private int maxWeightCapacity;
    private String type;

    public int getMaxWeightCapacity() {
        return maxWeightCapacity;
    }

    public int getBikeWeight() {
        return bikeWeight;
    }

    public String getType() {
        return type;
    }

    public void setMaxWeightCapacity(int maxWeightCapacity) {
        this.maxWeightCapacity = maxWeightCapacity;
    }

    public void setBikeWeight(int bikeWeight) {
        this.bikeWeight = bikeWeight;
    }

    public void setType(String type) {
        this.type = type;
    }
}
