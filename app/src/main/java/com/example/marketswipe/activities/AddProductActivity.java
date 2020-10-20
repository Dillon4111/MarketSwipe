package com.example.marketswipe.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marketswipe.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private DatabaseReference categoriesDB, subCategoriesDB;
    private Spinner catSpinner, subCatSpinner;
    private List<String> categories, subCategories;
    private TextView counter;
    private EditText editName, editPrice, editDescription;
    private Button addProductButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);

        catSpinner = findViewById(R.id.cat_spinner);
        subCatSpinner = findViewById(R.id.subcat_spinner);
        categories = new ArrayList<String>();
        categories.add("Select Category...");
        subCategories = new ArrayList<String>();
        subCategories.add("Select Sub-Category...");

        categoriesDB = FirebaseDatabase.getInstance().getReference("Categories");
        categoriesDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    //String category = categorySnapshot.child("Categories").getValue(String.class);
                    if (categorySnapshot != null) {
                        categories.add(categorySnapshot.getKey());
                        Log.d("CatSnap", categorySnapshot.getKey());
                    }
                }
                Log.d("All Cats", categories.toString());
                ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                        android.R.layout.simple_spinner_item, categories) {
                    @Override
                    public boolean isEnabled(int position) {
                        if (position == 0) {
                            // Disable the first item from Spinner
                            // First item will be use for hint
                            return false;
                        } else {
                            return true;
                        }
                    }

                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        if (position == 0) {
                            // Set the hint text color gray
                            tv.setTextColor(Color.GRAY);
                        } else {
                            tv.setTextColor(Color.BLACK);
                        }
                        return view;
                    }
                };
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                catSpinner.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddProductActivity.this, "Error", Toast.LENGTH_LONG).show();
            }
        });

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    subCategories.clear();
                    subCategories.add("Select Sub-Category...");
                    String selectedCategory = "Categories/" + parent.getItemAtPosition(position).toString();
                    //String selectedCategory = "Categories/" +
                    //       catSpinner.getSelectedItem().toString();
                    subCategoriesDB = FirebaseDatabase.getInstance().getReference(selectedCategory);
                    subCategoriesDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot subcategorySnapshot : snapshot.getChildren()) {
                                if (subcategorySnapshot != null) {
                                    subCategories.add(subcategorySnapshot.getValue().toString());
                                    //Log.d("SubCatSnap", "hello");
                                }
                            }
                            ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                                    android.R.layout.simple_spinner_item, subCategories) {
                                @Override
                                public boolean isEnabled(int position) {
                                    if (position == 0) {
                                        // Disable the first item from Spinner
                                        // First item will be use for hint
                                        return false;
                                    } else {
                                        return true;
                                    }
                                }

                                @Override
                                public View getDropDownView(int position, View convertView,
                                                            ViewGroup parent) {
                                    View view = super.getDropDownView(position, convertView, parent);
                                    TextView tv = (TextView) view;
                                    if (position == 0) {
                                        // Set the hint text color gray
                                        tv.setTextColor(Color.GRAY);
                                    } else {
                                        tv.setTextColor(Color.BLACK);
                                    }
                                    return view;
                                }
                            };
                            subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            subCatSpinner.setAdapter(subCategoryAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddProductActivity.this, "Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editName = findViewById(R.id.editProductName);
        editPrice = findViewById(R.id.editProductPrice);
        editDescription = findViewById(R.id.editDescription);
        counter = findViewById(R.id.counter);
        addProductButton = findViewById(R.id.addProductButton);

        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                counter.setText(String.valueOf(s.length()) + "/250");
            }

            public void afterTextChanged(Editable s) {
            }
        };
        editDescription.addTextChangedListener(mTextEditorWatcher);

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}

