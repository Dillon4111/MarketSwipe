package com.example.marketswipe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marketswipe.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText signInEmail, signInPassword;
    private Button registerButton;
    private TextView orSignInText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mAuth = FirebaseAuth.getInstance();

        signInEmail = findViewById(R.id.signInEmail);
        signInPassword = findViewById(R.id.signInPassword);
        registerButton = findViewById(R.id.signInButton);
        orSignInText = findViewById(R.id.orRegisterText);

        //FirebaseUser currentUser = mAuth.getCurrentUser();
        //update ui with current user
        //updateUI(currentUser);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInEmail.getText().toString();
                String password = signInPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Register", "createUserWithEmail:success");
                                    mUser = mAuth.getCurrentUser();
                                } else {
                                    Log.w("Register", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

     orSignInText.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Log.d("OR REGISTER", "Link clicked");
             Intent intent = new Intent(SignInActivity.this, RegistrationActivity.class);
             startActivity(intent);
         }
     });
    }

//    private void writeNewUser(String userId) {
//        List<Integer> product_ids = new ArrayList<>();
//        List<Integer> chathistory_ids = new ArrayList<>();
//        User user = new User("Dillon Rochford", "password", "dillon@email", "fb_id",
//                "12.123.1212.12", 5, 3.5, product_ids, chathistory_ids );
//
//        mDatabase.child("users").child(userId).setValue(user);
//    }
}