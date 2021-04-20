package com.example.marketswipe.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.marketswipe.R;
import com.example.marketswipe.models.Product;
import com.example.marketswipe.utils.ProductSearch;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysePricesActivity extends AppCompatActivity {

    private EditText analysePriceText;
    private String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_prices);

        analysePriceText = findViewById(R.id.analysePriceEditText);
        Button analysePriceButton = findViewById(R.id.analysePricesButton);
        analysePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                        StrictMode.setThreadPolicy(policy);

                        float ebayPrice = 0;
                        float amazonPrice = 0;
                        float doneDealPrice;

                        productName = analysePriceText.getText().toString();
                        ProductSearch productSearch = new ProductSearch(productName);

                        final float[] marketSwipePrice = {0};

                        Product ebayProduct = productSearch.getEbayProduct();
                        Product amazonProduct = new Product();
                        try {
                            amazonProduct = productSearch.getAmazonProduct();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Product doneDealProduct = productSearch.getDoneDealProduct();

                        String ebayStringPrice = ebayProduct.getWebPrice();
                        String amazonStringPrice = amazonProduct.getWebPrice();
                        String doneDealStringPrice = doneDealProduct.getWebPrice();

                        Pattern pat = Pattern.compile("(\\d+[.]\\d\\d)");
                        Matcher mat = pat.matcher(ebayStringPrice);

                        while (mat.find()) {
                            Log.d("THREAD - EBAY PRICE", mat.group());
                            ebayPrice = Float.parseFloat(mat.group());
                        }

                        Matcher mat2 = pat.matcher(amazonStringPrice);

                        while (mat2.find()) {
                            Log.d("THREAD - AMAZON PRICE", mat2.group());
                            amazonPrice = Float.parseFloat(mat2.group());
                        }

                        doneDealPrice = Float.parseFloat(doneDealStringPrice.replaceAll("[^\\d.]", ""));

                        final ArrayList<Double> productPrices = new ArrayList<>();

                        DatabaseReference productDB = FirebaseDatabase.getInstance().getReference("Products");
                        final float finalEbayPrice = ebayPrice;
                        final float finalDoneDealPrice = doneDealPrice;
                        final float finalAmazonPrice = amazonPrice;
                        productDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot prodSnapshot : snapshot.getChildren()) {
                                    Product product = prodSnapshot.getValue(Product.class);

                                    if (product.getName().contains(productName)) {
                                        productPrices.add(product.getPrice());
                                    }
                                }
                                for (Double value : productPrices) {
                                    marketSwipePrice[0] += value.doubleValue();
                                }
                                marketSwipePrice[0] /= productPrices.size();

                                BarChart barChart = (BarChart) findViewById(R.id.barchart);

                                ArrayList<BarEntry> entries = new ArrayList<>();
                                entries.add(new BarEntry(0f, finalEbayPrice));
                                entries.add(new BarEntry(1f, finalAmazonPrice));
                                entries.add(new BarEntry(2f, finalDoneDealPrice));
                                entries.add(new BarEntry(3f, marketSwipePrice));

                                BarDataSet bardataset = new BarDataSet(entries, "");

                                ArrayList<String> labels = new ArrayList<String>();
                                labels.add("eBay");
                                labels.add("Amazon");
                                labels.add("DoneDeal");
                                labels.add("MarketSwipe");

                                BarData data = new BarData(bardataset);
                                data.setBarWidth(1);
                                barChart.setData(data); // set the data and list of labels into chart
                                Description desc = new Description();
                                desc.setText("Current Prices of Product");
                                barChart.setDescription(desc);  // set the description
                                barChart.getXAxis().setLabelCount(data.getEntryCount());
                                barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                                barChart.getLegend().setEnabled(false);
                                barChart.animateY(5000);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
            }
        });
    }
}