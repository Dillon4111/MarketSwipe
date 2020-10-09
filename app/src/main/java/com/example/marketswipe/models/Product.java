package com.example.marketswipe.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Product {
    private int id, user_id, category_id, onlineprice_id;
    private String name, description;
    private double price;

    public Product() {
    }

    public Product(int id, int user_id, int category_id, int onlineprice_id,
                   String name, String description, double price) {
        this.id = id;
        this.user_id = user_id;
        this.category_id = category_id;
        this.onlineprice_id = onlineprice_id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getOnlineprice_id() {
        return onlineprice_id;
    }

    public void setOnlineprice_id(int onlineprice_id) {
        this.onlineprice_id = onlineprice_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
