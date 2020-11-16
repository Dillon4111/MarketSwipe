package com.example.marketswipe.models;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.marketswipe.R;
import com.example.marketswipe.activities.ProductDetailsActivity;
import com.example.marketswipe.utils.GlideApp;
import com.google.firebase.storage.StorageReference;
import com.mindorks.placeholderview.annotations.Animate;
import com.mindorks.placeholderview.annotations.Click;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.LongClick;
import com.mindorks.placeholderview.annotations.NonReusable;
import com.mindorks.placeholderview.annotations.Position;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;

@NonReusable
@Animate
@Layout(R.layout.item_gallery_image)
public class GalleryImage {

    @View(R.id.gallery_card_view)
    CardView cardView;

    @View(R.id.gallery_image_view)
    ImageView imageView;

    @Position
    int position;

    private Context context;
    //private String url;
    private StorageReference ref;

    public GalleryImage(Context context, StorageReference ref) {
        this.context = context;
        this.ref = ref;
    }

    /*
     * This method is called when the view is rendered
     * onResolved method could be named anything, Example: onAttach
     */
    @Resolve
    public void onResolved() {
        // do something here
        // example: load imageView with url image
//        RequestOptions options = new RequestOptions()
//                .centerCrop()
//                .placeholder(R.mipmap.ic_launcher_round)
//                .error(R.mipmap.ic_launcher_round);
//
//
//
//        Glide.with(context).load(url).apply(options).into(imageView);

//        Glide.with(context).load(url).placeholder(R.mipmap.ic_launcher_round).into(imageView);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(4f);
        circularProgressDrawable.setCenterRadius(60f);
        circularProgressDrawable.start();

        GlideApp.with(context)
                .load(ref)
                .placeholder(circularProgressDrawable)
                .into(imageView);
    }

    /*
     * This method is called when the view holder is recycled
     * and used to display view for the next data set
     */
//    @Recycle
//    public void onRecycled(){
//        // do something here
//        // Example: clear some references used by earlier rendering
//    }

    /*
     * This method is called when the view with id image_view is clicked.
     * onImageViewClick method could be named anything.
     */
    @Click(R.id.gallery_image_view)
    public void onImageViewClick(){
        // do something
    }

    @LongClick(R.id.gallery_image_view)
    public void onImageViewLongClick() {
        // do something
    }

}
