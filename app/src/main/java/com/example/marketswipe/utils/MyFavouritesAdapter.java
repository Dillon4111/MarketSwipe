package com.example.marketswipe.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.marketswipe.R;
import com.example.marketswipe.activities.ProductDetailsActivity;
import com.example.marketswipe.models.Product;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MyFavouritesAdapter extends RecyclerView.Adapter<MyFavouritesAdapter.MyViewHolder> {
    private ArrayList<Product> mylistvalues;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView priceView, nameView;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.productNameFavourite);
            priceView = itemView.findViewById(R.id.productPriceFavourite);
            imageView = itemView.findViewById(R.id.imageViewFavourite);
        }
    }

    public MyFavouritesAdapter(ArrayList<Product> myDataset, Context context) {
        mylistvalues = myDataset;
        this.context = context;
    }

    @Override
    public MyFavouritesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.favourite_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        final Product product = mylistvalues.get(position);
        holder.nameView.setText(product.getName());
        holder.priceView.setText("€" + product.getPrice());

        GlideApp.with(context)
                .load(storageReference.child(product.getImages().get(0)))
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("FAVOURITES PRODUCT", product.getName());
                Intent intent = new Intent(context, ProductDetailsActivity.class);
                intent.putExtra("PRODUCT_INTENT", product);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }

}
