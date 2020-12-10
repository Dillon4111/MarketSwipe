package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.marketswipe.R;
import com.example.marketswipe.models.GalleryImage;
import com.example.marketswipe.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.PlaceHolderView;;import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView productName, productPrice, productCategory, productSubCategory, productDescription;
    private StorageReference storageReference;
    Product product;
    private Button messageSellerButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String chatID;


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

        for(String url: product.getImages()) {

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
                        for(DataSnapshot chatSnap: snapshot.getChildren()) {
                            List<String> members = new ArrayList<>();
                            members = (List<String>) chatSnap.child("members").getValue();
                            if(members.contains(mUser.getUid()) && members.contains(product.getUser_id())) {
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
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_enter);
        finish();
    }
}