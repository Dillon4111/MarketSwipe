package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;

import com.example.marketswipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FavouritesActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Button textAjaiButon;
        textAjaiButon = findViewById(R.id.chatWithAjaiButton);


        Button textDillonButon;
        textDillonButon = findViewById(R.id.chatWithDillonButton);
        textDillonButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavouritesActivity.this, ChatActivity.class);
                i.putExtra("SECOND_ID", "Ue991QAog5XOJeGCqqIj7Rdfwux2");
                startActivity(i);
            }
        });

        textAjaiButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FavouritesActivity.this, ChatActivity.class);
                i.putExtra("SECOND_ID", "TmtzOXawBCM5uWqY7ZVyxGmUjvf1");
                startActivity(i);
            }
        });
    }
}