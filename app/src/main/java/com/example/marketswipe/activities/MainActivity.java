package com.example.marketswipe.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.marketswipe.utils.DistanceCalculator;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private SwipePlaceHolderView mSwipeView;
    private StorageReference storageReference;
    private String uid;
    private DrawerLayout drawerLayout;
    Double productLat, productLong, userLat, userLong, userDistance;
    Long userDistanceLong;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeView = null;

        List<Product> productList = new ArrayList<>();

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        uid = mUser.getUid();

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(uid)) {
                        userDistanceLong = (Long) userSnapshot.child("distance").getValue();
                        userDistance = userDistanceLong.doubleValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        createCards();

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

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
            case R.id.nav_messages:
                i = new Intent(MainActivity.this, ChatHistoryActivity.class);
                startActivity(i);
                break;
            case R.id.nav_my_products:
                i = new Intent(MainActivity.this, UserProductsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_favourites:
                i = new Intent(MainActivity.this, FavouritesActivity.class);
                startActivity(i);
                break;
            case R.id.nav_analyse_prices:
                i = new Intent(MainActivity.this, AnalysePricesActivity.class);
                startActivity(i);
                break;
            case R.id.map:
                i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_settings:
                i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.nav_log_out:
                mAuth.signOut();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Toast.makeText(MainActivity.this, "User signed out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                finish();
                startActivity(intent);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem item = menu.findItem(R.id.search_spinner);

        final Spinner catSpinner = (Spinner) item.getActionView();
        final List<String> categories = new ArrayList<String>();
        categories.add("Search...");

        DatabaseReference categoriesDB = FirebaseDatabase.getInstance().getReference("Categories");
        categoriesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    //String category = categorySnapshot.child("Categories").getValue(String.class);
                    if (categorySnapshot != null) {
                        categories.add(categorySnapshot.getKey());
                    }
                }
                categories.add("All");
                Log.d("All Cats", categories.toString());
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_item, categories) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                catSpinner.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });

        final String[] selectedCategory = new String[1];

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                mSwipeView.removeAllViews();
                switch (position) {
                    case 1:
                    case 2:
                    case 4:
                    case 3:
                    case 5:
                    case 6:
                        Toast.makeText(MainActivity.this, "Searching " + parent.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                        selectedCategory[0] = parent.getSelectedItem().toString();
                        searchCategory(selectedCategory[0]);
                        break;
                    case 7:
                        createCards();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.search_spinner:
                // do something
                Log.d("SEARCH", "HELLOOOOOOO");
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void createCards() {
        DatabaseReference productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    final Product product = productSnapshot.getValue(Product.class);

                    if (!product.getUser_id().equals(uid)) {
//                        final Double productLat, productLong, userLat, userLong;
                        product.setId(productSnapshot.getKey());
                        Log.d("MAIN PROD ID", product.getId());
                        DatabaseReference locationDB = FirebaseDatabase.getInstance().getReference("User_Location");
                        locationDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                                    if (locationSnapshot.getKey().equals(product.getUser_id())) {
                                        productLat = (Double) locationSnapshot.child("location").child("latitude").getValue();
                                        productLong = (Double) locationSnapshot.child("location").child("longitude").getValue();
                                    } else if (locationSnapshot.getKey().equals(uid)) {
                                        userLat = (Double) locationSnapshot.child("location").child("latitude").getValue();
                                        userLong = (Double) locationSnapshot.child("location").child("longitude").getValue();
                                    }
                                }
                                Double distance = null;
                                try {
                                    DistanceCalculator distanceCalculator = new DistanceCalculator();
                                    distance = distanceCalculator.distance(productLat, productLong, userLat, userLong, "K");
                                } catch (NullPointerException ignored) {
                                }

                                if (userDistance >= distance) {
                                    Card card = new Card(MainActivity.this, product, null,
                                            mSwipeView, storageReference.child(product.getImages().get(0)), distance);
                                    mSwipeView.addView(card);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);

        mSwipeView.getBuilder().setSwipeType(SwipePlaceHolderView.SWIPE_TYPE_HORIZONTAL)
                .setDisplayViewCount(4).setSwipeDecor(
                new SwipeDecor()
                        //.setPaddingTop(-50)
                        .setSwipeAnimFactor(0.75f)
                        .setSwipeRotationAngle(20)
                        .setPaddingLeft(30)
                        .setRelativeScale(0.01f));
    }

    public void searchCategory(final String category) {
        DatabaseReference productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    final Product product = productSnapshot.getValue(Product.class);

                    if (!product.getUser_id().equals(uid) && product.getCategory().equals(category)) {
//                        final Double productLat, productLong, userLat, userLong;
                        product.setId(productSnapshot.getKey());
                        Log.d("MAIN PROD ID", product.getId());
                        DatabaseReference locationDB = FirebaseDatabase.getInstance().getReference("User_Location");
                        locationDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot locationSnapshot : snapshot.getChildren()) {
                                    if (locationSnapshot.getKey().equals(product.getUser_id())) {
                                        productLat = (Double) locationSnapshot.child("location").child("latitude").getValue();
                                        productLong = (Double) locationSnapshot.child("location").child("longitude").getValue();
                                    } else if (locationSnapshot.getKey().equals(uid)) {
                                        userLat = (Double) locationSnapshot.child("location").child("latitude").getValue();
                                        userLong = (Double) locationSnapshot.child("location").child("longitude").getValue();
                                    }
                                }
                                Double distance = null;
                                try {
                                    DistanceCalculator distanceCalculator = new DistanceCalculator();
                                    distance = distanceCalculator.distance(productLat, productLong, userLat, userLong, "K");
                                } catch (NullPointerException ignored) {
                                }

                                if (userDistance >= distance) {
                                    Card card = new Card(MainActivity.this, product, null,
                                            mSwipeView, storageReference.child(product.getImages().get(0)), distance);
                                    mSwipeView.addView(card);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
