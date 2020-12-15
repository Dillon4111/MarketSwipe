package com.example.marketswipe.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.marketswipe.R;
import com.example.marketswipe.models.Card;
import com.example.marketswipe.utils.DistanceCalculator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private MarkerOptions marker = new MarkerOptions().title("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        final String[] name = new String[1];

        DatabaseReference locationDB = FirebaseDatabase.getInstance().getReference("User_Location");
        locationDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (final DataSnapshot locationSnapshot : snapshot.getChildren()) {
//                    if (mUser.getUid().equals(locationSnapshot.getKey())) {

                    DatabaseReference locationDB = FirebaseDatabase.getInstance().getReference("Users");
                    locationDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot lSnapshot : snapshot.getChildren()) {
                                if (marker.getTitle().equals("") && lSnapshot.getKey().equals(mUser.getUid())) {
                                    Double userLat = (Double) locationSnapshot.child("location").child("latitude").getValue();
                                    Double userLong = (Double) locationSnapshot.child("location").child("longitude").getValue();

                                    // mMap.clear();
                                    LatLng user = new LatLng(userLat, userLong);
                                    //mMap.addMarker(new MarkerOptions().position(user).title("You"));
                                    marker.position(user).title("You");
                                    mMap.addMarker(marker);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(user));

                                } else if (lSnapshot.getKey().equals(mUser.getUid())) {
                                    Double userLat = (Double) locationSnapshot.child("location").child("latitude").getValue();
                                    Double userLong = (Double) locationSnapshot.child("location").child("longitude").getValue();

                                    marker.position(new LatLng(userLat, userLong));

                                    Log.d("TAGGGGGGG", "HELLLOOOOOOOO");
                                } else if (locationSnapshot.getKey().equals(lSnapshot.getKey())) {
                                    name[0] = lSnapshot.child("username").getValue().toString();

                                    Double userLat = (Double) locationSnapshot.child("location").child("latitude").getValue();
                                    Double userLong = (Double) locationSnapshot.child("location").child("longitude").getValue();

                                    // mMap.clear();
                                    LatLng user = new LatLng(userLat, userLong);
                                    mMap.addMarker(new MarkerOptions().position(user).title(name[0]));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(user));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent i = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(i);
    }
}