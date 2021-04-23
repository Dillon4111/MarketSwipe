package com.example.marketswipe.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String username;
    private String email;
    private double distance;
    private List<String> favourites;


    public User() {
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.favourites = new ArrayList<>();
        this.distance = 25;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<String> getProductIds() {
        return favourites;
    }

    public void setProductIds(List<String> productIds) {
        this.favourites = productIds;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", distance=" + distance +
                ", favourites=" + favourites +
                '}';
    }
}
