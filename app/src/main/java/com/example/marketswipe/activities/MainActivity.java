package com.example.marketswipe.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;
import com.example.marketswipe.models.Card;
import com.example.marketswipe.models.Product;
import com.example.marketswipe.utils.WindowManager;
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

public class MainActivity extends AppCompatActivity {

    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private StorageReference storageReference;
    private List<Product> productList;
    private List<Bitmap> productCoverPhotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        productList = new ArrayList<>();

        FirebaseStorage storage = FirebaseStorage.getInstance();
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

        DatabaseReference productsDB = FirebaseDatabase.getInstance().getReference("Products");
        productsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                //do
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    final Product product = productSnapshot.getValue(Product.class);
                    productList.add(product);
                    Log.d("Product Snapshot", productSnapshot.getValue().toString());
                    Log.d("Product image 1", product.getImages().get(0));

                    storageReference.child(product.getImages().get(0))
                            .getBytes(Long.MAX_VALUE)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Use the bytes to display the image
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    //imageView.setImageBitmap(bitmap);
                                    productCoverPhotos.add(bitmap);
                                    Log.d("Product bitmap", bitmap.toString());
                                    Card card = new Card(MainActivity.this, product, bitmap, mSwipeView);
                                    mSwipeView.addView(card);
//                                    List<Object> cards = mSwipeView.getAllResolvers();
//                                    Log.i("SWIPEVIEW", String.valueOf(mSwipeView.getAllResolvers()));
//                                    Log.i("PRODUCT NAME", card.mProduct.getName());
                                }
                            });
                    count++;
                }
                //while (count < 3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSwipeView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        int bottomMargin = WindowManager.dpToPx(160); // if there is some view of size 160 dp
        int windowHeight = WindowManager.getDeviceHeight(MainActivity.this);
        int windowWidth = WindowManager.getDeviceWidth(MainActivity.this);

        mSwipeView.getBuilder().setDisplayViewCount(4).setSwipeDecor(
                new SwipeDecor()
                        .setPaddingTop(-50)
                        .setRelativeScale(0.01f));
//                .setViewWidth(windowSize.x)
//                .setViewHeight(windowSize.y - bottomMargin);
//                        .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
//                        .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));


//        for(Profile profile : Utils.loadProfiles(this.getApplicationContext())){
//            mSwipeView.addView(new TinderCard(mContext, profile, mSwipeView));
//        }


        findViewById(R.id.downBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSwipeView.getAllResolvers().size() == 0) {
                    Toast.makeText(MainActivity.this, "Please wait",
                            Toast.LENGTH_SHORT).show();
                } else {
                    List<Object> cards = mSwipeView.getAllResolvers();
                    Card card = (Card) cards.get(0);
                    Product product = card.mProduct;
                    Intent intent = new Intent(MainActivity.this, ProductDetailsActivity.class);
                    intent.putExtra("PRODUCT_INTENT", product);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_out_bottom, R.anim.slide_in_bottom);
                }
            }
        });

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeView.doSwipe(false);
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
