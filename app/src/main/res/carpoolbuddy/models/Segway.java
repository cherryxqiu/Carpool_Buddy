package com.example.carpoolbuddy.models;

public class Segway extends Vehicle{
    private int range;
    private int capacity;


    public int getWeightCapacity() {
        return capacity;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }


    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
