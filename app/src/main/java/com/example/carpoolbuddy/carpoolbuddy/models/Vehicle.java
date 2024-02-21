package com.example.carpoolbuddy.carpoolbuddy.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable {
    private User owner;
    private String model;
    private String contact;
    private int capacity;
    private String vehicleID;
    private ArrayList<String> ridersUIDs;
    private boolean isOpen = true;
    private boolean isEnded = false;
    private boolean isEndedByUser = false;

    private String vehicleType;
    private double price;
    private CTime time;

    private CLocation pickUpLocation;
    private CLocation dropOffLocation;

    // Constructors
    public Vehicle() {}

    public Vehicle(String id, User owner, int capacity, double price, String type, CLocation pickUp, CLocation dropOff, CTime time) {
        this.vehicleID = id;
        this.owner = owner;
        this.capacity = capacity;
        this.price = price;
        this.vehicleType = type;
        this.pickUpLocation = pickUp;
        this.dropOffLocation = dropOff;
        this.time = time;
    }

    // Getters and Setters
    public boolean isEndedByUser() {
        return isEndedByUser;
    }

    public void setEndedByUser(boolean endedByUser) {
        this.isEndedByUser = endedByUser;
    }

    public CTime getTime() {
        return time;
    }

    public void setTime(CTime time) {
        this.time = time;
    }

    public CLocation getDropOffLocation() {
        return dropOffLocation;
    }

    public CLocation getPickUpLocation() {
        return pickUpLocation;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setDropOffLocation(CLocation dropOffLocation) {
        this.dropOffLocation = dropOffLocation;
    }

    public void setPickUpLocation(CLocation pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public User getOwner() {
        return owner;
    }

    public String getModel() {
        return model;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public String getContact() {
        return contact;
    }

    public void setEnded(boolean ended) {
        this.isEnded = ended;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public ArrayList<String> getRidersUIDs() {
        return ridersUIDs;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public double getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public void setRidersUIDs(ArrayList<String> ridersUIDs) {
        this.ridersUIDs = ridersUIDs;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
