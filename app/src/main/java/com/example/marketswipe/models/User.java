package com.example.marketswipe.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String username;
    private String email;
    private String facebookid;
    private String gpslocation;
    private int num_ratings;
    private double rating, distance;
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

    public String getFacebookid() {
        return facebookid;
    }

    public void setFacebookid(String facebookid) {
        this.facebookid = facebookid;
    }

    public String getGpslocation() {
        return gpslocation;
    }

    public void setGpslocation(String gpslocation) {
        this.gpslocation = gpslocation;
    }

    public int getNum_ratings() {
        return num_ratings;
    }

    public void setNum_ratings(int num_ratings) {
        this.num_ratings = num_ratings;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
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
                ", facebookid='" + facebookid + '\'' +
                ", gpslocation='" + gpslocation + '\'' +
                ", num_ratings=" + num_ratings +
                ", rating=" + rating +
                ", distance=" + distance +
                ", favourites=" + favourites +
                '}';
    }
}
