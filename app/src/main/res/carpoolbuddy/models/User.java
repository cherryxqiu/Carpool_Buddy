package com.example.carpoolbuddy.models;

import java.util.ArrayList;

public class User {
    private String uid;
    private String name;
    private String email;
    private String userType;
    private String imageUrl;
    private String phone;
    private double priceMultiplier;
    public ArrayList<Vehicle> ownedVehicles= new ArrayList<Vehicle>();;
    private double rating = 5.00;
    private double ratingNum = 0;
    private double ratingTotal = 0;

    public User(){

    }

    public double getRatingNum() {
        return ratingNum;
    }

    public double getRatingTotal() {
        return ratingTotal;
    }

    public void setRatingTotal(double ratingTotal) {
        this.ratingTotal = ratingTotal;
    }

    public void setRatingNum(double ratingNum) {
        this.ratingNum = ratingNum;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public User(String uid, String username, String email, String type){
        ownedVehicles= new ArrayList<Vehicle>();
        this.uid = uid;
        this.email = email;
        this.name = username;
        this.userType = type;
    }

    public User(String username, String email, String phone, String imageUrl, int t){
        ownedVehicles= new ArrayList<Vehicle>();
        this.email = email;
        this.name = username;
        this.phone = phone;
        this.imageUrl = imageUrl;
    }
    public User( String username,String email,  String phone){
        ownedVehicles= new ArrayList<Vehicle>();
        this.email = email;
        this.name = username;
        this.phone = phone;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }

    public void setPriceMultiplier(double priceMultiplier) {
        this.priceMultiplier = priceMultiplier;
    }

    public ArrayList<Vehicle> getOwnedVehicles() {
        return ownedVehicles;
    }

    public void setOwnedVehicles(ArrayList<Vehicle> ownedVehicles) {
        this.ownedVehicles = ownedVehicles;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void addVehicle(Vehicle v) {
        this.ownedVehicles.add(v);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}