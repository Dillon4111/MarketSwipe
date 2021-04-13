package com.example.marketswipe.utils;

import android.util.Log;

import com.example.marketswipe.models.AmazonProduct;
import com.example.marketswipe.models.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductSearch {

    private String productName;

    public ProductSearch(String productName) {
        this.productName = productName;
    }

    public Product getEbayProduct() {
        Product ebayProduct = null;
        //String productName = product.getName();
        Document doc = null;
        {
            try {
                doc = Jsoup.connect("https://www.ebay.ie/sch/i.html?_nkw=" + productName).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Elements allDiv = doc.getElementsByClass("s-item      ");

        for (Element span : allDiv) {
            String name = span.getElementsByTag("h3").text();
            Log.d("EBAY NAME", name);

            String price = span.getElementsByClass("s-item__price").text();
            Log.d("EBAY PRICE", price);

            Element imageElement = span.select("img").first();
            String absoluteUrl = imageElement.absUrl("src");
            Log.d("EBAY IMAGE", absoluteUrl);

            Element link = span.select("div.s-item__image > a").first();
            String url = link.attr("href");
            Log.d("EBAY URL", url);

            ebayProduct = new Product(name, price, url, absoluteUrl, "ebay");

            break;
        }

        Log.d("EBAY PRODUCT", ebayProduct.toString());

        return ebayProduct;
    }

    public Product getAmazonProduct() throws IOException {
        Product product = new Product();
        OkHttpClient client = new OkHttpClient();

        //String productName = this.product.getName();

        Request request = new Request.Builder()
                .url("https://amazon-price1.p.rapidapi.com/search?keywords=" + productName + "&marketplace=GB")
                .get()
                .addHeader("x-rapidapi-key", "1f4fe5a185msh82a7267a7ea761ap1da7dbjsne4a45ced50f9")
                .addHeader("x-rapidapi-host", "amazon-price1.p.rapidapi.com")
                .build();

        Response response = null;

        try {
            response = client.newCall(request).execute();

            String responseBodyString = response.body().string();
            Log.d("AMAZON RESPONSE", responseBodyString);

            List<AmazonProduct> resultList = new Gson().fromJson(responseBodyString, new TypeToken<ArrayList<AmazonProduct>>(){}.getType());

            AmazonProduct amazonProduct = resultList.get(0);

            Log.d("TOSTRING", amazonProduct.toString());

            product.setName(amazonProduct.getTitle());
            product.setWebPrice(amazonProduct.getPrice());
            product.setWebUrl(amazonProduct.getDetailPageURL());
            product.setImageUrl(amazonProduct.getImageUrl());
            product.setSite("amazon");

            Log.d("AMAZON PRICE", String.valueOf(product.getPrice()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return product;
    }

    public Product getDoneDealProduct() {
        Product product = null;
        //String productName = this.product.getName();
        Document doc = null;
        {
            try {
                doc = Jsoup.connect("https://www.donedeal.ie/all?words=" + productName).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Elements allDiv = doc.getElementsByClass("card-item");

        for (Element span : allDiv) {
            String name = span.getElementsByClass("card__body-title").text();

            Element imageElement = span.select("img").first();
            String absoluteUrl = imageElement.absUrl("src");

            String price = span.getElementsByClass("card__price").text();

            Element link = span.select("li.card-item > a").first();
            String url = link.attr("href");

            product = new Product(name, price, url, absoluteUrl, "donedeal");

            break;
        }

        Log.d("DONEDEAL PRICE", String.valueOf(product.getPrice()));

        Log.d("DONEDEAL PRODUCT", product.toString());

        return product;
    }
}
