package com.example.marketswipe.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emredavarci.noty.Noty;
import com.example.marketswipe.R;
import com.example.marketswipe.config.Config;
import com.example.marketswipe.models.AmazonProduct;
import com.example.marketswipe.models.GalleryImage;
import com.example.marketswipe.models.Product;
import com.example.marketswipe.utils.MyFavouritesAdapter;
import com.example.marketswipe.utils.ProductSearch;
import com.example.marketswipe.utils.WebResultsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindorks.placeholderview.PlaceHolderView;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;;import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductDetailsActivity extends AppCompatActivity {

    Product product;
    private FirebaseUser mUser;
    String chatID;
    RecyclerView myRecyclerView;
    private ArrayList<Product> myDataset = new ArrayList<Product>();
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(Config.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        Intent i = getIntent();
        product = (Product) i.getSerializableExtra("PRODUCT_INTENT");

        TextView productName = findViewById(R.id.productName);
        TextView productPrice = findViewById(R.id.productPrice);
        TextView productCategory = findViewById(R.id.productCategory);
        TextView productSubCategory = findViewById(R.id.productSubCategory);
        TextView productDescription = findViewById(R.id.productDescription);

        productName.setText(product.getName());
        productPrice.setText("€" + product.getPrice());
        productCategory.setText(product.getCategory());
        productSubCategory.setText(product.getSub_category());
        productDescription.setText(product.getDescription());


        PlaceHolderView phvGallery = (PlaceHolderView) findViewById(R.id.phv_gallery);

        phvGallery.getBuilder()
                .setHasFixedSize(false)
                .setItemViewCacheSize(10)
                .setLayoutManager(new LinearLayoutManager(
                        ProductDetailsActivity.this,
                        LinearLayoutManager.HORIZONTAL,
                        false));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        for (String url : product.getImages()) {
            phvGallery.addView(new GalleryImage(getApplicationContext(), storageReference.child(url)));
        }

        Button messageSellerButton = findViewById(R.id.messageSellerButton);
        messageSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference chatsDB = FirebaseDatabase.getInstance().getReference("Chats");
                chatID = UUID.randomUUID().toString();
                chatsDB.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot chatSnap : snapshot.getChildren()) {
                            List<String> members;
                            members = (List<String>) chatSnap.child("members").getValue();
                            if (members.contains(mUser.getUid()) && members.contains(product.getUser_id())) {
                                chatID = chatSnap.getKey();
                                Log.d("CHATID", chatID);
                                break;
                            }
                        }
                        Intent i = new Intent(ProductDetailsActivity.this, ChatActivity.class);
                        i.putExtra("SECOND_ID", product.getUser_id());
                        i.putExtra("CHAT_ID", chatID);
                        startActivity(i);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        Button buyButton = findViewById(R.id.buyNowButton);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailsActivity.this, PayPalService.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                startService(intent);

                processPayment();
            }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        myRecyclerView = (RecyclerView) findViewById(R.id.webResultsRecyclerView);
        myRecyclerView.setHasFixedSize(true);
        LinearLayoutManager myLayoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        myRecyclerView.setLayoutManager(myLayoutManager);

        ProductSearch productSearch = new ProductSearch(product.getName());
        myDataset.add(productSearch.getEbayProduct());
        try {
            myDataset.add(productSearch.getAmazonProduct());
        } catch (IOException e) {
            e.printStackTrace();
        }
        myDataset.add(productSearch.getDoneDealProduct());

        myRecyclerView.setLayoutManager(new LinearLayoutManager((ProductDetailsActivity.this)));
        myRecyclerView.setHasFixedSize(true);
        WebResultsAdapter mAdapter = new WebResultsAdapter(myDataset, ProductDetailsActivity.this);
        myRecyclerView.setAdapter(mAdapter);
    }

    private void processPayment() {
        String amount = String.valueOf(product.getPrice());
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount),"EUR",
                "Purchase Goods",PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(ProductDetailsActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);

        startActivityForResult(intent,7777);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 7777) {
//            if (resultCode == RESULT_OK) {
//                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirmation != null) {
//                    try {
//                        String paymentDetails = confirmation.toJSONObject().toString(4);
//                        startActivity(new Intent(this, PaymentDetails.class)
//                                .putExtra("Payment Details", paymentDetails)
//                                .putExtra("Amount", String.valueOf(product.getPrice())));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED)
//                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
//        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
//            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_enter);
        finish();
    }
}