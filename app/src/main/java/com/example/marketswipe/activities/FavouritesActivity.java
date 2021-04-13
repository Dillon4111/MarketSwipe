package com.example.marketswipe.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import com.example.marketswipe.models.Product;
import com.example.marketswipe.utils.MyFavouritesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FavouritesActivity extends AppCompatActivity {

    private FirebaseUser mUser;

    private ArrayList<Product> myDataset= new ArrayList<Product>();
    private MyFavouritesAdapter mAdapter;
    RecyclerView myRecyclerView;

    private List<String> productIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        myRecyclerView= (RecyclerView) findViewById(R.id.myFavouritesRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager= new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("Users");
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

        DatabaseReference productsDB = FirebaseDatabase.getInstance().getReference("Products");
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
                    new ItemTouchHelper(itemTouch).attachToRecyclerView(myRecyclerView);
                    myRecyclerView.setAdapter(mAdapter);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ImageButton backButton = findViewById(R.id.favouritesBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavouritesActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });
    }
    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    //.addBackgroundColor(ContextCompat.getColor(UserProductsActivity.this, R.color.grey))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Toast.makeText(FavouritesActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
            final Product product = myDataset.get(viewHolder.getAdapterPosition());
            Log.d("PRODUCT ID", product.getId());
            myDataset.remove(viewHolder.getAdapterPosition());
            mAdapter.notifyDataSetChanged();

            final DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");
//            productDB.child(mUser.getUid()).child("Favourites").child(product.getId()).removeValue();
            userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot userSnap: snapshot.getChildren()) {
                        if(userSnap.getKey().equals(mUser.getUid())) {
                            try {
                                List<String> favourites = (List<String>) userSnap.child("favourites").getValue();
                                favourites.remove(product.getId());
                                userDB.child(userSnap.getKey()).child("favourites").setValue(favourites);

                            }
                            catch (NullPointerException ignored) {}
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            myDataset.clear();
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}