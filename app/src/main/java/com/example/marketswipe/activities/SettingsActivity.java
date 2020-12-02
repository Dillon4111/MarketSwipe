package com.example.marketswipe.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String SILENT_MODE = "silentMode";

    private Switch silentSwitch;
    private boolean silent;
    private static SeekBar seek_bar;
    private static TextView text_view;
    private Button applySettings;
    private ImageButton backButton;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private double userDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        seekbar();

        Context context = getApplicationContext();
        final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        silent = settings.getBoolean(SILENT_MODE, false);

        silentSwitch = findViewById(R.id.silentSwitch);

        if (!silent) {
            silentSwitch.setText("Notifications Off");
        } else {
            silentSwitch.setText("Notifications On");
        }

        silentSwitch.setChecked(silent);

        silentSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!silent) {
                    silentSwitch.setChecked(true);
                    silentSwitch.setText("Notifications On");
                    silent = true;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SILENT_MODE, true);
                    editor.commit();
                } else {
                    silentSwitch.setChecked(false);
                    silentSwitch.setText("Notifications Off");
                    silent = false;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SILENT_MODE, false);
                    editor.commit();
                }
            }
        });

        applySettings = findViewById(R.id.applySettingsButton);
        applySettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uid = mUser.getUid();
                DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("Users").child(uid);
                usersDB.child("distance").setValue(seek_bar.getProgress());
            }
        });

        backButton = findViewById(R.id.settingsBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });
    }

    public void seekbar() {
        seek_bar = findViewById(R.id.seekBar);
        text_view = findViewById(R.id.textView);

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot: snapshot.getChildren()) {
                    if(userSnapshot.getKey().equals(mUser.getUid())) {
                        Long userDistanceLong = (Long) userSnapshot.child("distance").getValue();
                        userDistance = userDistanceLong.doubleValue();

                        Log.d("User distance", String.valueOf(userDistance).replace(".0",""));

                        if (userDistance == 0) {
                            text_view.setText("Distance: " + seek_bar.getProgress() + "/" + seek_bar.getMax() + "km");
                        } else {
                            text_view.setText("Distance: " + userDistance + "/" + seek_bar.getMax() + "km");
                            seek_bar.setProgress(Integer.parseInt(String.valueOf(userDistance).replace(".0","")));
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seek_bar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        text_view.setText("Distance: " + seek_bar.getProgress() + "/" + seek_bar.getMax() + "km");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        text_view.setText("Distance: " + seek_bar.getProgress() + "/" + seek_bar.getMax() + "km");
                    }
                }
        );

    }
}
