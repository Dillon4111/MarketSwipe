package com.example.marketswipe.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.marketswipe.R;
import com.example.marketswipe.utils.GlideApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.util.ArrayList;
import java.util.List;

@NonReusable
@Layout(R.layout.card_view)
public class Card {

    @View(R.id.productCoverImageView)
    private ImageView productImageView;

    @View(R.id.nameAgeTxt)
    private TextView productInfoTxt;

    @View(R.id.locationNameTxt)
    private TextView locationTxt;

    public Product mProduct;
    private Context mContext;
    private Bitmap bitmap;
    private SwipePlaceHolderView mSwipeView;
    private StorageReference mRef;

    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    private Double mLocation;

    List<String> favs = new ArrayList<>();

    public Card(Context context, Product product, Bitmap bitmap,
                SwipePlaceHolderView swipeView, StorageReference ref, Double location) {
        mContext = context;
        mProduct = product;
        //this.bitmap = bitmap;
        mSwipeView = swipeView;
        mRef = ref;
        mLocation = location;
    }

    @Resolve
    private void onResolved() {
        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(mContext);
        circularProgressDrawable.setStrokeWidth(3f);
        circularProgressDrawable.setCenterRadius(50f);
        circularProgressDrawable.start();

        GlideApp.with(mContext)
                .load(mRef)
                .placeholder(circularProgressDrawable)
                .into(productImageView);
        productInfoTxt.setText(mProduct.getName() + ", " + mProduct.getPrice());
        locationTxt.setText(String.format("%1$,.2f", mLocation) + "km");
    }

    @SwipeOut
    private void onSwipedOut() {
        Log.d("EVENT", "onSwipedOut");
        //mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn() {
        Log.d("EVENT", "onSwipedIn");
        //mSwipeView.addView(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        final String uid = mUser.getUid();

        final DatabaseReference db = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid)
                .child("favourites");

        DatabaseReference usersDB = FirebaseDatabase.getInstance().getReference("Users");
        usersDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.getKey().equals(uid)) {

                        if (userSnapshot.child("favourites").exists()) {
                            favs = (List<String>) userSnapshot.child("favourites").getValue();
                            Log.d("IF", "HELLO");
                        } else {
                            Log.d("ELSE", "HELLO");
                        }
                        //Log.d("FAVS", favs.toString());
                        Log.d("User ID", uid);
                        Log.d("P ID", mProduct.getId());

                        favs.add(mProduct.getId());
                        db.setValue(favs);
                        favs.clear();

                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SwipeInState
    private void onSwipeInState() {
        Log.d("EVENT", "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState() {
        Log.d("EVENT", "onSwipeOutState");
    }

    @Click(R.id.productCoverImageView)
    public Product onImageViewClick() {
        // do something
        Log.d("EVENT", "onClickState");
        return mProduct;
    }
}