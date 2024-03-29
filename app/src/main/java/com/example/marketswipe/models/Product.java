package com.example.marketswipe.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class Product implements Serializable {
    private String id;
    private String user_id;
    private String name, description, category, sub_category;
    private double price;
    private List<String> images;
    private String webUrl;
    private String webPrice;
    private String imageUrl;
    private String site;

    public Product() {
    }

    public Product(String user_id, String name, String description, double price,
                   String category, String sub_category, List<String> images) {
        this.user_id = user_id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.sub_category = sub_category;
        this.images = images;
    }

    public Product(String name, String price, String webUrl, String imageUrl, String site) {
        this.name = name;
        this.webPrice = price;
        this.webUrl = webUrl;
        this.imageUrl = imageUrl;
        this.site = site;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getWebPrice() {
        return webPrice;
    }

    public void setWebPrice(String webPrice) {
        this.webPrice = webPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}
