package com.example.marketswipe.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;
import com.example.marketswipe.models.Card;
import com.example.marketswipe.models.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.List;

//        imageView = findViewById(R.id.productImage);
//
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//
//        storageReference.child("/images/0ae4a5a4-f762-44b2-88f8-7a0a6f8c79cc")
//                .getBytes(Long.MAX_VALUE)
//                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Use the bytes to display the image
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                imageView.setImageBitmap(bitmap);
//            }
//        });

public class MainActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private DatabaseReference productsDB;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private List<Product> productList = new ArrayList<>();
    private List<Bitmap> productCoverPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        storageReference.child("/images/0ae4a5a4-f762-44b2-88f8-7a0a6f8c79cc")
//                .getBytes(Long.MAX_VALUE)
//                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                // Use the bytes to display the image
//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                //imageView.setImageBitmap(bitmap);
//                mSwipeView.addView(new Card(mContext, bitmap, mSwipeView));
//            }
//        });

        productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;

                do
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Product p = productSnapshot.getValue(Product.class);
                    productList.add(p);
                    Log.d("Product Snapshot", productSnapshot.getValue().toString());
                    Log.d("Product image 1", p.getImages().get(0));

                    storageReference.child(p.getImages().get(0))
                            .getBytes(Long.MAX_VALUE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Use the bytes to display the image
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    //imageView.setImageBitmap(bitmap);
                                    productCoverPhotos.add(bitmap);
                                    Log.d("Product bitmap", bitmap.toString());
                                }
                            });
                    count++;
                }
                while (count < 3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f));
//                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
//                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


//        for(Profile profile : Utils.loadProfiles(this.getApplicationContext())){
//            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
//        }


//        for (Bitmap bitmap : productCoverPhotos) {
//            mSwipeView.addView(new Card(mContext, bitmap, mSwipeView));
//            Log.d("Product bitmap", bitmap.toString());
//        }


        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mSwipeView.doSwipe(false);
                int count = 0;
                for (Bitmap bitmap : productCoverPhotos) {
                    mSwipeView.addView(new Card(mContext, productList.get(count), bitmap, mSwipeView));
                    Log.d("Product bitmap", bitmap.toString());
                    count++;
                }
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(true);
            }
        });
    }
}
