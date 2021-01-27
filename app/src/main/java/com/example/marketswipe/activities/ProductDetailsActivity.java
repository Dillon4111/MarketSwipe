package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketswipe.R;
import com.example.marketswipe.models.AmazonProduct;
import com.example.marketswipe.models.GalleryImage;
import com.example.marketswipe.models.Product;
import com.example.marketswipe.utils.MyFavouritesAdapter;
import com.example.marketswipe.utils.WebResultsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindorks.placeholderview.PlaceHolderView;;import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView productName, productPrice, productCategory, productSubCategory, productDescription;
    private StorageReference storageReference;
    Product product;
    private Button messageSellerButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String chatID;
    RecyclerView myRecyclerView;
    private WebResultsAdapter mAdapter;
    private ArrayList<Product> myDataset = new ArrayList<Product>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);

//        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent i = getIntent();
        product = (Product) i.getSerializableExtra("PRODUCT_INTENT");

        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productCategory = findViewById(R.id.productCategory);
        productSubCategory = findViewById(R.id.productSubCategory);
        productDescription = findViewById(R.id.productDescription);

        productName.setText(product.getName());
        productPrice.setText(String.valueOf(product.getPrice()));
        productCategory.setText(product.getCategory());
        productSubCategory.setText(product.getSub_category());
        productDescription.setText(product.getDescription());


        PlaceHolderView phvGallery = (PlaceHolderView) findViewById(R.id.phv_gallery);

        phvGallery.getBuilder()
                .setHasFixedSize(false)
                .setItemViewCacheSize(10)
                .setLayoutManager(new LinearLayoutManager(
                        ProductDetailsActivity.this,
                        LinearLayoutManager.HORIZONTAL,
                        false));
        //new GridLayoutManager(this, 4));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        for (String url : product.getImages()) {

            phvGallery.addView(new GalleryImage(getApplicationContext(), storageReference.child(url)));
        }

        messageSellerButton = findViewById(R.id.messageSellerButton);
        messageSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference chatsDB = FirebaseDatabase.getInstance().getReference("Chats");
                chatID = UUID.randomUUID().toString();
                chatsDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot chatSnap : snapshot.getChildren()) {
                            List<String> members = new ArrayList<>();
                            members = (List<String>) chatSnap.child("members").getValue();
                            if (members.contains(mUser.getUid()) && members.contains(product.getUser_id())) {
                                chatID = chatSnap.getKey();
                                Log.d("CHATID", chatID);
                                break;
                            }
                        }
                        Intent i = new Intent(ProductDetailsActivity.this, ChatActivity.class);
                        i.putExtra("SECOND_ID", product.getUser_id());
                        i.putExtra("CHAT_ID", chatID);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        myRecyclerView = (RecyclerView) findViewById(R.id.webResultsRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        myRecyclerView.setLayoutManager(myLayoutManager);
        myDataset.add(getEbayProduct());
        try {
            myDataset.add(getAmazonProduct());
        } catch (IOException e) {
            e.printStackTrace();
        }
        myRecyclerView.setLayoutManager(new LinearLayoutManager((ProductDetailsActivity.this)));
        myRecyclerView.setHasFixedSize(true);
        mAdapter = new WebResultsAdapter(myDataset, ProductDetailsActivity.this);
        myRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_enter);
        finish();
    }

    public Product getEbayProduct() {
        Product ebayProduct = null;
        String productName = product.getName();
        Document doc = null;
        {
            try {
                doc = Jsoup.connect("https://www.ebay.ie/sch/i.html?_nkw=" + productName).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Elements allDiv = doc.getElementsByClass("s-item    ");

        for (Element span : allDiv) {
            String name = span.getElementsByTag("h3").text();
            Log.d("EBAY NAME", name);

            String price = span.getElementsByClass("ITALIC").text();
            Log.d("EBAY PRICE", price);

            Element imageElement = span.select("img").first();
            String absoluteUrl = imageElement.absUrl("src");
            Log.d("EBAY IMAGE", absoluteUrl);

            Element link = span.select("div.s-item__image > a").first();
            String url = link.attr("href");
            Log.d("EBAY URL", url);

            ebayProduct = new Product(name, price, url, absoluteUrl);

            break;
        }

        return ebayProduct;
    }

    public Product getAmazonProduct() throws IOException {
        Product product = new Product();
        OkHttpClient client = new OkHttpClient();

        String productName = this.product.getName();

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
            Log.d("RESPONSE", responseBodyString);

            List<AmazonProduct> resultList = new Gson().fromJson(responseBodyString, new TypeToken<ArrayList<AmazonProduct>>(){}.getType());

            AmazonProduct amazonProduct = resultList.get(0);

            Log.d("TOSTRING", amazonProduct.toString());

            product.setName(amazonProduct.getTitle());
            product.setWebPrice(amazonProduct.getPrice());
            product.setWebUrl(amazonProduct.getDetailPageURL());
            product.setImageUrl(amazonProduct.getImageUrl());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return product;
    }
}