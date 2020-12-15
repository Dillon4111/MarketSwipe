package com.example.marketswipe.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.ablanco.zoomy.Zoomy;
import com.example.marketswipe.R;
import com.example.marketswipe.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddProductActivity extends AppCompatActivity {

    private boolean imageUploadSuccess;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference db;
    private DatabaseReference categoriesDB, subCategoriesDB;
    private Spinner catSpinner, subCatSpinner;
    private List<String> categories, subCategories;
    private TextView counter;
    private EditText editName, editPrice, editDescription;
    private Button addProductButton, chooseImagesButton;
    private List<Uri> uriList;
    private List<String> images;
    private List<ImageView> imageViews;
    FirebaseStorage storage;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        catSpinner = findViewById(R.id.cat_spinner);
        subCatSpinner = findViewById(R.id.subcat_spinner);
        categories = new ArrayList<String>();
        categories.add("*Select Category...");
        subCategories = new ArrayList<String>();
        subCategories.add("*Select Sub-Category...");

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

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    subCategories.clear();
                    subCategories.add("*Select Sub-Category...");
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


        chooseImagesButton = findViewById(R.id.chooseImagesButton);
        chooseImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(AddProductActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(AddProductActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });


        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseDatabase.getInstance().getReference();
                mAuth = FirebaseAuth.getInstance();
                mUser = mAuth.getCurrentUser();
                String uid = mUser.getUid();
                String productName = editName.getText().toString();
                String productPrice = editPrice.getText().toString();
                double priceDouble = Double.parseDouble(productPrice);
                String productDescription = editDescription.getText().toString();

                if (productName.matches("") || catSpinner.getSelectedItemPosition() == 0 ||
                        productPrice.matches("") || subCatSpinner.getSelectedItemPosition() == 0) {

                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(AddProductActivity.this);
                    dlgAlert.setMessage("Please fill in required fields(*)");
                    dlgAlert.setTitle("Hold Up!");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                } else {
                    String productCategory = catSpinner.getSelectedItem().toString();
                    String productSubCategory = subCatSpinner.getSelectedItem().toString();

                    if (!uploadImages()) {
                        Toast.makeText(AddProductActivity.this, "Cancelled, image upload error. Try again",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        Product product = new Product(uid, productName, productDescription, priceDouble,
                                productCategory, productSubCategory, images);
                        db.child("Products").push().setValue(product)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AddProductActivity.this, "Product added",
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AddProductActivity.this, "Write to db failed", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }
        });

        ImageButton backButton = findViewById(R.id.addProductBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddProductActivity.this, MainActivity.class);
                finish();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            final ImageView imageView1 = findViewById(R.id.imageView);
            final ImageView imageView2 = findViewById(R.id.imageView2);
            final ImageView imageView3 = findViewById(R.id.imageView3);
            final ImageView imageView4 = findViewById(R.id.imageView4);
            imageViews = new ArrayList<>();
            imageViews.add(imageView1);
            imageViews.add(imageView2);
            imageViews.add(imageView3);
            imageViews.add(imageView4);
            for (ImageView imageView : imageViews) {
                Zoomy.Builder builder = new Zoomy.Builder(this).target(imageView);
                builder.register();
            }

            final List<Bitmap> bitmaps = new ArrayList<>();
            ClipData clipData = data.getClipData();
            uriList = new ArrayList<>();

            if (clipData != null) {

                for (int i = 0; i < clipData.getItemCount(); i++) {
                    Uri imageUri = clipData.getItemAt(i).getUri();
                    uriList.add(imageUri);
                    try {
                        InputStream is = getContentResolver().openInputStream(imageUri);

                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        bitmaps.add(bitmap);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {

                Uri imageUri = data.getData();
                uriList.add(imageUri);
                try {
                    InputStream is = getContentResolver().openInputStream(imageUri);

                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    bitmaps.add(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < bitmaps.size(); i++) {
                imageViews.get(i).setImageBitmap(bitmaps.get(i));
            }
        }
    }

    public boolean uploadImages() {
        imageUploadSuccess = true;
        if (uriList != null) {
            images = new ArrayList<>();
            for (Uri uri : uriList) {
                StorageReference ref
                        = storageReference
                        .child("images/" + UUID.randomUUID().toString());

                ref.putFile(uri)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                imageUploadSuccess = false;
                                // Error, Image not uploaded
                                Toast
                                        .makeText(AddProductActivity.this,
                                                "Failed " + e.getMessage(),
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        })
                        .addOnProgressListener(
                                new OnProgressListener<UploadTask.TaskSnapshot>() {

                                    // Progress Listener for loading
                                    // percentage on the dialog box
                                    @Override
                                    public void onProgress(
                                            UploadTask.TaskSnapshot taskSnapshot) {
                                        double progress
                                                = (100.0
                                                * taskSnapshot.getBytesTransferred()
                                                / taskSnapshot.getTotalByteCount());
                                    }
                                });
                Log.i("REF PATH", ref.getPath());
                images.add(ref.getPath());
            }
        }
        return imageUploadSuccess;
    }

}

