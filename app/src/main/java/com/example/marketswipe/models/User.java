package com.example.marketswipe.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String username, password, email, facebookid, gpslocation;
    private int num_ratings;
    private double rating;
    private List<Integer> product_ids = new ArrayList<Integer>();
    private List<Integer> chathistory_ids = new ArrayList<Integer>();


    public User() {
    }

    public User(String username, String password, String email, String facebookid,
                String gpslocation, int num_ratings, double rating, List<Integer> product_ids,
                List<Integer> chathistory_ids) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.facebookid = facebookid;
        this.gpslocation = gpslocation;
        this.num_ratings = num_ratings;
        this.rating = rating;
        this.product_ids = product_ids;
        this.chathistory_ids = chathistory_ids;
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

    public List<Integer> getProduct_ids() {
        return product_ids;
    }

    public void setProduct_ids(List<Integer> product_ids) {
        this.product_ids = product_ids;
    }

    public List<Integer> getChathistory_ids() {
        return chathistory_ids;
    }

    public void setChathistory_ids(List<Integer> chathistory_ids) {
        this.chathistory_ids = chathistory_ids;
    }
}
