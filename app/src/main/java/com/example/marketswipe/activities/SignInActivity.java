package com.example.marketswipe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
    private EditText signInEmail, signInPassword;
    private Button signInButton;
    private TextView orSignInText;

    private Button testProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        testProduct = findViewById(R.id.button);
        testProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();

        signInEmail = findViewById(R.id.signInEmail);
        signInPassword = findViewById(R.id.signInPassword);
        signInButton = findViewById(R.id.signInButton);
        orSignInText = findViewById(R.id.orRegisterText);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signInEmail.getText().toString().trim();
                String password = signInPassword.getText().toString();

                if(email.matches("") || password.matches("")){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SignInActivity.this);

                    dlgAlert.setMessage("Please fill in both fields");
                    dlgAlert.setTitle("Hold up!");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();

                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                }
                else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignInActivity.this, "User signed in",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignInActivity.this, AddProductActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Log.w("MySignin", "SignInUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignInActivity.this, "Authentication failed",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
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
}