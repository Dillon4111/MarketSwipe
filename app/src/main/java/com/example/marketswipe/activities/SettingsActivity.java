package com.example.marketswipe.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;

public class SettingsActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String SILENT_MODE = "silentMode";

    private Switch silentSwitch;
    private boolean silent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Context context = getApplicationContext();
        final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);

        silent = settings.getBoolean(SILENT_MODE, false);

        silentSwitch = findViewById(R.id.silentSwitch);

        if(!silent) {
            silentSwitch.setText("Notifications Off");
        }
        else {
            silentSwitch.setText("Notifications On");
        }

        silentSwitch.setChecked(silent);

        silentSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!silent) {
                    silentSwitch.setChecked(true);
                    silentSwitch.setText("Notifications On");
                    silent = true;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SILENT_MODE, true);
                    editor.commit();
                }
                else {
                    silentSwitch.setChecked(false);
                    silentSwitch.setText("Notifications Off");
                    silent = false;
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean(SILENT_MODE, false);
                    editor.commit();
                }
            }
        });
    }
}
