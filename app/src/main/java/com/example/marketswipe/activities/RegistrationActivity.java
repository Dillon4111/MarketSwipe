package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;

public class RegistrationActivity extends AppCompatActivity {

    private TextView signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        signInButton = findViewById(R.id.backToSignIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Back to sign-in", "Link clicked");
                Intent intent = new Intent(RegistrationActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}
