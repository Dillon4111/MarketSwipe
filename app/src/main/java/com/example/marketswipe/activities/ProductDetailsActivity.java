package com.example.marketswipe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;
import com.example.marketswipe.models.Product;

public class ProductDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productdetails);

        Intent i = getIntent();
        Product product = (Product) i.getSerializableExtra("PRODUCT_INTENT");

        TextView textView = findViewById(R.id.testView);
        textView.setText(product.getName());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_enter, R.anim.slide_out_enter);
    }
}
