package com.example.marketswipe.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalysePricesActivity extends AppCompatActivity {

    private EditText analysePriceText;
    private Button analysePriceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse_prices);

        analysePriceText = findViewById(R.id.analysePriceEditText);
        analysePriceButton = findViewById(R.id.analysePricesButton);
        analysePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                final float[] ebayPrice = new float[1];
//                final float[] amazonPrice = new float[1];
//                final float[] doneDealPrice = new float[1];
//
//                Thread thread = new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try  {
//                            String productName = analysePriceText.getText().toString();
//                            ProductSearch productSearch = new ProductSearch(productName);
//                            ebayPrice[0] = (float)productSearch.getEbayProduct().getPrice();
//                            try {
//                                amazonPrice[0] = (float) productSearch.getAmazonProduct().getPrice();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            doneDealPrice[0] = (float) productSearch.getDoneDealProduct().getPrice();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                thread.start();
//
//
//                Thread thread2 = new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        try  {
//                            //Your code goes here
//                            BarChart barChart = (BarChart) findViewById(R.id.barchart);
//
//                            ArrayList<BarEntry> entries = new ArrayList<>();
//                            entries.add(new BarEntry(0f, ebayPrice[0]));
//                            entries.add(new BarEntry(1f, amazonPrice[0]));
//                            entries.add(new BarEntry(2f, doneDealPrice[0]));
//                            entries.add(new BarEntry(3f, 3f));
//
//                            BarDataSet bardataset = new BarDataSet(entries, "");
//
//                            ArrayList<String> labels = new ArrayList<String>();
//                            labels.add("eBay");
//                            labels.add("Amazon");
//                            labels.add("DoneDeal");
//                            labels.add("MarketSwipe");
//
//                            BarData data = new BarData(bardataset);
//                            data.setBarWidth(1);
//                            barChart.setData(data); // set the data and list of labels into chart
//                            Description desc = new Description();
//                            desc.setText("Current Prices of Product");
//                            barChart.setDescription(desc);  // set the description
//                            barChart.getXAxis().setLabelCount(data.getEntryCount());
//                            barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
//                            bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
//                            barChart.getLegend().setEnabled(false);
//                            barChart.animateY(5000);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//                try {
//                    thread2.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if(thread.getState()==Thread.State.TERMINATED){
//                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                        StrictMode.setThreadPolicy(policy);

                        float ebayPrice = 0;
                        float amazonPrice = 0;
                        float doneDealPrice = 0;

                        String productName = analysePriceText.getText().toString();
                        ProductSearch productSearch = new ProductSearch(productName);

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

                        doneDealPrice = Float.parseFloat(doneDealStringPrice.replaceAll("[^\\d.]", ""));;

//                        Log.d("THREAD - AMAZON PRICE", String.valueOf(amazonPrice));
//                        Log.d("THREAD - DONEDEAL PRICE", String.valueOf(doneDealPrice));

                        BarChart barChart = (BarChart) findViewById(R.id.barchart);

                        ArrayList<BarEntry> entries = new ArrayList<>();
                        entries.add(new BarEntry(0f, ebayPrice));
                        entries.add(new BarEntry(1f, amazonPrice));
                        entries.add(new BarEntry(2f, doneDealPrice));
                        entries.add(new BarEntry(3f, 3f));

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
                });
            }
        });
    }
}