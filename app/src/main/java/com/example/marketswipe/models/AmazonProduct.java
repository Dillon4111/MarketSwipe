package com.example.marketswipe.models;

public class AmazonProduct {
    private String ASIN,
            title,
            price,
            listPrice,
            imageUrl,
            detailPageURL,
            rating,
            totalReviews,
            subtitle,
            isPrimeEligible;

    public AmazonProduct() {
    }

    public AmazonProduct(String ASIN, String title, String price, String listPrice,
                         String imageUrl, String detailPageURL, String rating, String totalReviews,
                         String subtitle, String isPrimeEligible) {
        this.ASIN = ASIN;
        this.title = title;
        this.price = price;
        this.listPrice = listPrice;
        this.imageUrl = imageUrl;
        this.detailPageURL = detailPageURL;
        this.rating = rating;
        this.totalReviews = totalReviews;
        this.subtitle = subtitle;
        this.isPrimeEligible = isPrimeEligible;
    }

    public String getASIN() {
        return ASIN;
    }

    public void setASIN(String ASIN) {
        this.ASIN = ASIN;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getListPrice() {
        return listPrice;
    }

    public void setListPrice(String listPrice) {
        this.listPrice = listPrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDetailPageURL() {
        return detailPageURL;
    }

    public void setDetailPageURL(String detailPageURL) {
        this.detailPageURL = detailPageURL;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(String totalReviews) {
        this.totalReviews = totalReviews;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getIsPrimeEligible() {
        return isPrimeEligible;
    }

    public void setIsPrimeEligible(String isPrimeEligible) {
        this.isPrimeEligible = isPrimeEligible;
    }

    @Override
    public String toString() {
        return "AmazonProduct{" +
                "ASIN='" + ASIN + '\'' +
                ", title='" + title + '\'' +
                ", price='" + price + '\'' +
                ", listPrice='" + listPrice + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", detailPageURL='" + detailPageURL + '\'' +
                ", rating='" + rating + '\'' +
                ", totalReviews='" + totalReviews + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", isPrimeEligible='" + isPrimeEligible + '\'' +
                '}';
    }
}
