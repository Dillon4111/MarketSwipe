package com.example.marketswipe.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.marketswipe.R;
import com.example.marketswipe.models.Product;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WebResultsAdapter extends RecyclerView.Adapter<WebResultsAdapter.MyViewHolder> {
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

    public WebResultsAdapter(ArrayList<Product> myDataset, Context context) {
        mylistvalues = myDataset;
        this.context = context;
    }

    @Override
    public WebResultsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.favourite_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Product product = mylistvalues.get(position);

        if(product.getSite().equals("ebay")) {
            holder.nameView.setText("eBay:\n" + product.getName());
        }
        else if(product.getSite().equals("donedeal")) {
            holder.nameView.setText("DoneDeal:\n" + product.getName());
        }
        else{
            holder.nameView.setText("Amazon:\n" + product.getName());
        }

        holder.priceView.setText(product.getWebPrice());

        Picasso.get().load(product.getImageUrl()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(product.getWebUrl()));

                context.startActivity(browse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mylistvalues.size();
    }

}
