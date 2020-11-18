package com.example.marketswipe.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.marketswipe.R;
import com.example.marketswipe.models.Card;
import com.example.marketswipe.models.Product;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private StorageReference storageReference;
    private List<Product> productList;
    private List<Bitmap> productCoverPhotos = new ArrayList<>();
    private Toolbar toolbar;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productList = new ArrayList<>();

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        DatabaseReference productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    final Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
//                    Log.d("Product Snapshot", productSnapshot.getValue().toString());
//                    Log.d("Product image 1", product.getImages().get(0));

                    Card card = new Card(MainActivity.this, product, null,
                            mSwipeView, storageReference.child(product.getImages().get(0)));
                    mSwipeView.addView(card);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);

        mSwipeView.getBuilder().setDisplayViewCount(4).setSwipeDecor(
                new SwipeDecor()
                        //.setPaddingTop(-50)
                        .setPaddingLeft(30)
                        .setRelativeScale(0.01f));


        findViewById(R.id.downBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwipeView.getAllResolvers().size() == 0) {
                    Toast.makeText(MainActivity.this, "Please wait",
                            Toast.LENGTH_SHORT).show();
                } else {
                    List<Object> cards = mSwipeView.getAllResolvers();
                    Card card = (Card) cards.get(0);
                    Product product = card.mProduct;
                    Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_INTENT", product);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                }
            }
        });

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.openNavDrawer,
                R.string.closeNavDrawer
        );

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent i;
        switch (id) {
            case R.id.nav_add_product:
                i = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(i);
                break;
            case R.id.nav_my_products:
                Log.i("Menu", "1");
                break;
            case R.id.nav_favourites:
                Log.i("Menu", "2");
                break;
            case R.id.nav_recent:
                Log.i("Menu", "3");
                break;
            case R.id.nav_settings:
                i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_log_out:
                Log.i("Menu", "5");
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
