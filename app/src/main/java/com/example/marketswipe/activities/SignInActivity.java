package com.example.marketswipe.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.marketswipe.R;
import com.example.marketswipe.utils.LocationService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText signInEmail, signInPassword;
    private Button signInButton;
    private TextView orSignInText;

    private static final int PERMISSIONS_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

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


                                        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
                                        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                            finish();
                                        }

                                        int permission = ContextCompat.checkSelfPermission(SignInActivity.this,
                                                Manifest.permission.ACCESS_FINE_LOCATION);

                                        if (permission == PackageManager.PERMISSION_GRANTED) {

                                            if(startTrackerService()){
                                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }

                                        } else {

                                            ActivityCompat.requestPermissions(SignInActivity.this,
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    PERMISSIONS_REQUEST);
                                        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startTrackerService();
        } else {

            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean startTrackerService() {
        startService(new Intent(SignInActivity.this, LocationService.class));

        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

        return true;
    }
}