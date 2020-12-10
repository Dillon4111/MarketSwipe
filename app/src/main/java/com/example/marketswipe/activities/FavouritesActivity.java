package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;

import com.example.marketswipe.models.Product;
import com.example.marketswipe.utils.MyFavouritesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.marketswipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private ArrayList<Product> myDataset= new ArrayList<Product>();
    private MyFavouritesAdapter mAdapter;
    RecyclerView myRecyclerView;

    private DatabaseReference usersDB, productsDB;
    private List<String> productIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        myRecyclerView= (RecyclerView) findViewById(R.id.myFavouritesRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager= new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        usersDB = FirebaseDatabase.getInstance().getReference("Users");
        usersDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot noteSnapshot : snapshot.getChildren()) {
                    if(mUser.getUid().equals(noteSnapshot.getKey())) {
                        productIds = (List<String>) noteSnapshot.child("favourites").getValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    try {
                        if (productIds.contains(productSnapshot.getKey())) {
                            String name = productSnapshot.child("name").getValue().toString();
                            String price = productSnapshot.child("price").getValue().toString();
                            String description = productSnapshot.child("description").getValue().toString();
                            List<String> images = (List<String>) productSnapshot.child("images").getValue();
                            String productID = productSnapshot.getKey();
                            Product product = new Product();
                            product.setId(productID);
                            product.setName(name);
                            product.setPrice(Double.parseDouble(price));
                            product.setImages(images);
                            product.setDescription(description);
                            product.setCategory(productSnapshot.child("category").getValue().toString());
                            product.setSub_category(productSnapshot.child("sub_category").getValue().toString());
                            product.setUser_id(productSnapshot.child("user_id").getValue().toString());
                            myDataset.add(product);
                        }
                    }
                    catch (NullPointerException ignored) {}
                    }
                    myRecyclerView.setLayoutManager(new LinearLayoutManager((FavouritesActivity.this)));
                    myRecyclerView.setHasFixedSize(true);
                    mAdapter = new MyFavouritesAdapter(myDataset, FavouritesActivity.this);
                    myRecyclerView.setAdapter(mAdapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Button textAjaiButon;
//        textAjaiButon = findViewById(R.id.chatWithAjaiButton);
//
//
//        Button textDillonButon;
//        textDillonButon = findViewById(R.id.chatWithDillonButton);
//        textDillonButon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(FavouritesActivity.this, ChatActivity.class);
//                i.putExtra("SECOND_ID", "Ue991QAog5XOJeGCqqIj7Rdfwux2");
//                startActivity(i);
//            }
//        });
//
//        textAjaiButon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(FavouritesActivity.this, ChatActivity.class);
//                i.putExtra("SECOND_ID", "TmtzOXawBCM5uWqY7ZVyxGmUjvf1");
//                startActivity(i);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}