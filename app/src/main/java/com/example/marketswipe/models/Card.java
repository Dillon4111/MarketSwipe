package com.example.marketswipe.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.marketswipe.R;
import com.example.marketswipe.utils.GlideApp;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

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

    private Double mLocation;

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
        mSwipeView.addView(this);
    }

    @SwipeCancelState
    private void onSwipeCancelState() {
        Log.d("EVENT", "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn() {
        Log.d("EVENT", "onSwipedIn");
        mSwipeView.addView(this);
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
    public Product onImageViewClick(){
        // do something
        Log.d("EVENT", "onClickState");
        return mProduct;
    }


//    @SwipingDirection
//    public void onSwipingDirection(SwipeDirection direction, Product product) {
//        if(direction == SwipeDirection.TOP) {
//            Log.i("UP", product.getName());
//            Log.d("DEBUG", "SwipingDirection " + direction.name());
//        }
//    }
}