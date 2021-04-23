package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;
import com.example.marketswipe.models.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private EditText input;
    private String uid, chatID;
    private FirebaseListAdapter<ChatMessage> adapter;

    String secondUID, userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_room);

        secondUID = null;

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        uid = mUser.getUid();

        List<String> members = new ArrayList<>();

        members.add(uid);

        FloatingActionButton fab = findViewById(R.id.fab);
        input = findViewById(R.id.input);

        Intent i = getIntent();
        secondUID = i.getExtras().getString("SECOND_ID");
        chatID = i.getExtras().getString("CHAT_ID");

        Log.d("SECOND UID CHATACTIVITY", secondUID);
        Log.d("CHAT ID", chatID);

        members.add(secondUID);

        ScrollView scrollView = findViewById(R.id.scrollView);


        DatabaseReference chatMessageRef = FirebaseDatabase.getInstance().getReference("Chat_Messages");

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");
        userDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if (userSnap.getKey().equals(uid)) {
                        userName = userSnap.child("username").getValue().toString();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance()
                .getReference("Chats")
                .child(chatID)
                .child("members").setValue(members);


        FirebaseDatabase.getInstance()
                .getReference("User_Chats")
                .child(uid)
                .child(chatID)
                .setValue(true);

        FirebaseDatabase.getInstance()
                .getReference("User_Chats")
                .child(secondUID)
                .child(chatID)
                .setValue(true);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = input.getText().toString();

                DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
                Date dateobj = new Date();
                final ChatMessage message = new ChatMessage(messageText, df.format(dateobj), uid, userName, chatID);

                FirebaseDatabase.getInstance()
                        .getReference("Chat_Messages")
                        .child(chatID)
                        .push()
                        .setValue(message);

            }
        });

        Log.d("CHATID", chatID);
        displayMessages();
    }

    public void displayMessages() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Chat_Messages").child(chatID);
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        final FirebaseListOptions<ChatMessage> options =
                new FirebaseListOptions.Builder<ChatMessage>()
                        .setQuery(query, ChatMessage.class)
                        .setLayout(R.layout.message)
                        .build();
        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(@NotNull View v, @NotNull ChatMessage model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);
                messageText.setText(model.getMessage());
                messageUser.setText(model.getUserName());
                messageTime.setText(model.getDate());

                input.setText("");

                adapter.notifyDataSetChanged();
            }
        };
        listOfMessages.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}