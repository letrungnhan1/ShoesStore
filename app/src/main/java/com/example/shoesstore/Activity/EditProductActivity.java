package com.example.shoesstore.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityEditProductBinding;
import com.example.shoesstore.utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;

public class EditProductActivity extends AppCompatActivity {

    private ActivityEditProductBinding binding;
    private DatabaseReference databaseReference;
    private String productId;
    private Product product;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        productId = getIntent().getStringExtra("product_id");

        loadProductDetails();

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.textAddImage.setOnClickListener(v -> selectImage());

        binding.buttonSaveProduct.setOnClickListener(v -> saveProductDetails());
    }

    private void loadProductDetails() {
        databaseReference.child(Constants.KEY_COLLECTION_PRODUCTS).child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.getValue(Product.class);
                if (product != null) {
                    binding.editTextTitle.setText(product.getTitle());
                    binding.editTextPrice.setText(String.valueOf(product.getPrice()));
                    binding.editTextOldPrice.setText(String.valueOf(product.getOldPrice()));
                    binding.editTextDescription.setText(product.getDescription());
                    binding.editTextSizes.setText(TextUtils.join(", ", product.getSizes()));
                    binding.switchIsHot.setChecked(product.isHot());
                    Glide.with(EditProductActivity.this)
                            .load(product.getPicUrl())
                            .into(binding.imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProductActivity.this, "Failed to load product details: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.imageProfile.setImageURI(imageUri);
        }
    }

    private void saveProductDetails() {
        String title = binding.editTextTitle.getText().toString().trim();
        double price = Double.parseDouble(binding.editTextPrice.getText().toString().trim());
        double oldPrice = Double.parseDouble(binding.editTextOldPrice.getText().toString().trim());
        String description = binding.editTextDescription.getText().toString().trim();
        List<String> sizes = Arrays.asList(binding.editTextSizes.getText().toString().trim().split("\\s*,\\s*"));
        boolean isHot = binding.switchIsHot.isChecked();

        product.setTitle(title);
        product.setPrice(price);
        product.setOldPrice(oldPrice);
        product.setDescription(description);
        product.setSizes(sizes);
        product.setHot(isHot);

        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        product.setPicUrl(uri.toString());
                        updateProductInDatabase();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(EditProductActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            updateProductInDatabase();
        }
    }

    private void updateProductInDatabase() {
        databaseReference.child(Constants.KEY_COLLECTION_PRODUCTS).child(product.getId()).setValue(product)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProductActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProductActivity.this, "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        String type = getContentResolver().getType(uri);
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
    }
}

