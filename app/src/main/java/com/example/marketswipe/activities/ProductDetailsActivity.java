package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketswipe.R;
import com.example.marketswipe.models.GalleryImage;
import com.example.marketswipe.models.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.PlaceHolderView;;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView productName, productPrice, productCategory, productSubCategory, productDescription;
    private StorageReference storageReference;
    Product product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);

//        getSupportActionBar().hide();

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

// (Optional): If customization is Required then use Builder with the PlaceHolderView
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

        for(String url: product.getImages()) {

            phvGallery.addView(new GalleryImage(getApplicationContext(), storageReference.child(url)));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_enter);
    }

    public void getImageUrls() {
    }
}