package com.example.marketswipe.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Product {
    private int id;
    private int user_id;
    private int onlineprice_id;
    private String name, description, category, sub_category;
    private double price;
    //PICTURES

    public Product() {
    }

    public Product(int id, int user_id, String name, String description, double price,
                   String category, String sub_category) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.sub_category = sub_category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }
}
