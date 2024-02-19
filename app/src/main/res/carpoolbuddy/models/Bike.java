package com.example.carpoolbuddy.models;

public class Bike extends Vehicle{

    private int weight;
    private int weightCapacity;
    private String bycicleType;

    public int getWeightCapacity() {
        return weightCapacity;
    }

    public int getWeight() {
        return weight;
    }

    public String getBycicleType() {
        return bycicleType;
    }

    public void setWeightCapacity(int weightCapacity) {
        this.weightCapacity = weightCapacity;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setBycicleType(String bycicleType) {
        this.bycicleType = bycicleType;
    }
}
