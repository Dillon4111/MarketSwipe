package com.example.marketswipe.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.marketswipe.R;
import com.example.marketswipe.activities.ChatActivity;
import com.example.marketswipe.activities.ChatHistoryActivity;
import com.example.marketswipe.activities.ProductDetailsActivity;
import com.example.marketswipe.models.ChatMessage;
import com.example.marketswipe.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyChatsAdapter extends RecyclerView.Adapter<MyChatsAdapter.MyViewHolder> {
    private ArrayList<ChatMessage> mylistvalues;
    private DatabaseReference chatsDB;
    private StorageReference storageReference;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    String chatID, secondUID;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameView, messageView, timeView;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.userNameChats);
            imageView = itemView.findViewById(R.id.imageViewChats);
            messageView = itemView.findViewById(R.id.messageChats);
            timeView = itemView.findViewById(R.id.messageTimeChats);
        }
    }

    public MyChatsAdapter(ArrayList<ChatMessage> myDataset, Context context) {
        mylistvalues = myDataset;
        this.context = context;
    }

    @Override
    public MyChatsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.chat_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        //        final FirebaseStorage storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        secondUID = null;

        final ChatMessage chatMessage = mylistvalues.get(position);
        chatsDB = FirebaseDatabase.getInstance().getReference("Chats");
        chatsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot chatSnap : snapshot.getChildren()) {
                    if (chatSnap.getKey().equals(chatMessage.getChatID())) {
                        List<String> ids = (List<String>) chatSnap.child("members").getValue();
                        if (ids.get(0).equals(mUser.getUid()))
                            secondUID = ids.get(1);
                        else if (!ids.get(0).equals(mUser.getUid()))
                            secondUID = ids.get(0);

                        Log.d("HEYHEYHEY", secondUID);

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("Users");
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    if (userSnap.getKey().equals(secondUID)) {
                        holder.nameView.setText(userSnap.child("username").getValue().toString());
                        holder.messageView.setText(chatMessage.getMessage());
                        holder.timeView.setText(chatMessage.getDate());

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        GlideApp.with(context)
//                .load(storageReference.child(product.getImages().get(0)))
//                .into(holder.imageView);
        //TODO load user image

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatsDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot chatSnap : snapshot.getChildren()) {
                            if (chatSnap.getKey().equals(chatMessage.getChatID())) {
                                List<String> ids = (List<String>) chatSnap.child("members").getValue();
                                if (ids.get(0).equals(mUser.getUid()))
                                    secondUID = ids.get(1);
                                else if (!ids.get(0).equals(mUser.getUid()))
                                    secondUID = ids.get(0);

                                Log.d("HEYHEYHEY", secondUID);

                                break;
                            }
                        }
                        Log.d("SECONDUIUIUIUI", secondUID);
                        DatabaseReference chatsDB = FirebaseDatabase.getInstance().getReference("Chats");
                        //chatID = UUID.randomUUID().toString();
                        chatsDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot chatSnap : snapshot.getChildren()) {
                                    List<String> members = new ArrayList<>();
                                    members = (List<String>) chatSnap.child("members").getValue();
                                    if (members.contains(mUser.getUid()) && members.contains(secondUID)) {
                                        chatID = chatSnap.getKey();
                                        Log.d("CHATID", chatID);
                                        break;
                                    }
                                }
                                Intent i = new Intent(context, ChatActivity.class);
                                i.putExtra("SECOND_ID", secondUID);
                                i.putExtra("CHAT_ID", chatID);
                                context.startActivity(i);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }
}

