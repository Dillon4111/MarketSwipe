package com.example.marketswipe.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String username, password, email, facebookid, gpslocation;
    private int num_ratings;
    private double rating;


    public User() {
    }

    public User(String username, String password, String email, String facebookid,
                String gpslocation, int num_ratings, double rating) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.facebookid = facebookid;
        this.gpslocation = gpslocation;
        this.num_ratings = num_ratings;
        this.rating = rating;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

}
