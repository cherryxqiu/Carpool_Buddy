package com.example.carpoolbuddy.carpoolbuddy.models;

import java.util.ArrayList;

public class User {
    private String userID;
    private String fullName;
    private String emailAddress;
    private String userType;
    private String profileImageUrl;
    private String phoneNumber;
    private double multiplierPrice;
    public ArrayList<Vehicle> ownedVehicles = new ArrayList<>();
    private double userRating = 5.00;
    private double totalRatingsCount = 0;
    private double totalRatingsSum = 0;

    public User() {}

    public User(String userID, String fullName, String emailAddress, String userType) {
        this.userID = userID;
        this.emailAddress = emailAddress;
        this.fullName = fullName;
        this.userType = userType;
    }

    public User(String fullName, String emailAddress, String phoneNumber, String profileImageUrl) {
        this.emailAddress = emailAddress;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
    }

    public User(String fullName, String emailAddress, String phoneNumber) {
        this.emailAddress = emailAddress;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getMultiplierPrice() {
        return multiplierPrice;
    }

    public void setMultiplierPrice(double multiplierPrice) {
        this.multiplierPrice = multiplierPrice;
    }

    public ArrayList<Vehicle> getOwnedVehicles() {
        return ownedVehicles;
    }

    public void setOwnedVehicles(ArrayList<Vehicle> ownedVehicles) {
        this.ownedVehicles = ownedVehicles;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void addVehicle(Vehicle vehicle) {
        this.ownedVehicles.add(vehicle);
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public double getTotalRatingsCount() {
        return totalRatingsCount;
    }

    public void setTotalRatingsCount(double totalRatingsCount) {
        this.totalRatingsCount = totalRatingsCount;
    }

    public double getTotalRatingsSum() {
        return totalRatingsSum;
    }

    public void setTotalRatingsSum(double totalRatingsSum) {
        this.totalRatingsSum = totalRatingsSum;
    }
}
