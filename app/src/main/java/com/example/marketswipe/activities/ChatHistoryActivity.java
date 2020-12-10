package com.example.marketswipe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.example.marketswipe.R;
import com.example.marketswipe.models.ChatMessage;
import com.example.marketswipe.models.Product;
import com.example.marketswipe.utils.MyChatsAdapter;
import com.example.marketswipe.utils.MyFavouritesAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity {

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private MyChatsAdapter mAdapter;
    RecyclerView myRecyclerView;

    private DatabaseReference usersDB, chatsDB, messagesDB;
    private List<String> chatIDs = new ArrayList<>();
    private ArrayList<ChatMessage> lastMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        myRecyclerView= (RecyclerView) findViewById(R.id.myChatHistoryRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager= new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        chatsDB = FirebaseDatabase.getInstance().getReference("User_Chats");
        chatsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot chatSnap: snapshot.getChildren()) {
                    if(chatSnap.getKey().equals(mUser.getUid())) {
                        for(DataSnapshot chatidSnap: chatSnap.getChildren()) {
                            chatIDs.add(chatidSnap.getKey());
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messagesDB = FirebaseDatabase.getInstance().getReference("Chat_Messages");
        messagesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(String s: chatIDs) {
                    ChatMessage message = new ChatMessage();
                    for (DataSnapshot chatSnap : snapshot.getChildren()) {
                        if (chatSnap.getKey().equals(s)) {
                            for(DataSnapshot messageSnap: chatSnap.getChildren()) {
                                message.setMessage(messageSnap.child("message").getValue().toString());
                                message.setDate(messageSnap.child("date").getValue().toString());
                                message.setUid(messageSnap.child("uid").getValue().toString());
                                message.setUserName(messageSnap.child("userName").getValue().toString());
                                message.setChatID(messageSnap.child("chatID").getValue().toString());
                            }
                            break;
                        }
                    }
                    lastMessages.add(message);
//                    Log.d("LASTMESSAGE", message.getMessage());
//                    Log.d("SECOND ID ID ID", message.getUid());
                }
                myRecyclerView.setLayoutManager(new LinearLayoutManager((ChatHistoryActivity.this)));
                myRecyclerView.setHasFixedSize(true);
                mAdapter= new MyChatsAdapter(lastMessages, ChatHistoryActivity.this);
                myRecyclerView.setAdapter(mAdapter);

                mAdapter.notifyDataSetChanged();
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
    }
}