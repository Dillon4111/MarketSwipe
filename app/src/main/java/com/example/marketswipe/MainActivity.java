package com.example.marketswipe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.marketswipe.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        String uid = UUID.randomUUID().toString();
        writeNewUser(uid);
    }

    private void writeNewUser(String userId) {
        List<Integer> product_ids = new ArrayList<>();
        List<Integer> chathistory_ids = new ArrayList<>();
        User user = new User("Dillon Rochford", "password", "dillon@email", "fb_id",
                "12.123.1212.12", 5, 3.5, product_ids, chathistory_ids );

        mDatabase.child("users").child(userId).setValue(user);
    }
}