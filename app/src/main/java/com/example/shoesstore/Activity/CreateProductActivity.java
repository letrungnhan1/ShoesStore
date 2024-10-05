package com.example.shoesstore.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.shoesstore.Domain.Category;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityCreateProductBinding;
import com.example.shoesstore.utilities.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.Manifest;

public class CreateProductActivity extends AppCompatActivity {

    private ActivityCreateProductBinding binding;
    private DatabaseReference databaseReference;
    private Uri imageUri;
    private List<Category> categoryList;
    private List<String> categoryTitles;
    private String selectedCategoryId;

    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        categoryList = new ArrayList<>();
        categoryTitles = new ArrayList<>();

        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.imageProfile.setOnClickListener(v -> selectImage());
        binding.buttonSaveProduct.setOnClickListener(v -> uploadImageAndSaveProduct());

        loadCategories();
    }

    private void loadCategories() {
        databaseReference.child(Constants.KEY_COLLECTION_CATEGORY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                categoryTitles.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        category.setId(dataSnapshot.getKey());
                        categoryList.add(category);
                        categoryTitles.add(category.getTitle());
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateProductActivity.this, android.R.layout.simple_spinner_item, categoryTitles);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerCategory.setAdapter(adapter);
                binding.spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedCategoryId = categoryList.get(position).getId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedCategoryId = null;
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateProductActivity.this, "Failed to load categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateProductActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else {
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_CAMERA || requestCode == PICK_IMAGE_GALLERY) {
                imageUri = data.getData();

                try {
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Bitmap resizedBitmap = resizeBitmap(bitmap, 373, 300);

                    // Convert bitmap to Uri
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContentResolver(), resizedBitmap, "Title", null);
                    imageUri = Uri.parse(path);

                    binding.textAddImage.setVisibility(View.GONE);
                    binding.imageProfile.setImageURI(imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private void uploadImageAndSaveProduct() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    saveProductToDatabase(imageUrl);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateProductActivity.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            saveProductToDatabase(null);
        }
    }

    private void saveProductToDatabase(String imageUrl) {
        String title = binding.editTextTitle.getText().toString().trim();
        String priceStr = binding.editTextPrice.getText().toString().trim();
        String oldPriceStr = binding.editTextOldPrice.getText().toString().trim();
        String description = binding.editTextDescription.getText().toString().trim();
        String sizesStr = binding.editTextSizes.getText().toString().trim(); // Lấy giá trị từ MultiAutoCompleteTextView
        boolean isHot = binding.switchIsHot.isChecked(); // Lấy giá trị của switch

        if (!title.isEmpty() && !priceStr.isEmpty() && !oldPriceStr.isEmpty() && !description.isEmpty() && selectedCategoryId != null) {
            double price = Double.parseDouble(priceStr);
            double oldPrice = Double.parseDouble(oldPriceStr);
            String id = databaseReference.push().getKey();

            // Chuyển đổi chuỗi kích thước thành danh sách
            List<String> sizes = Arrays.asList(sizesStr.split("\\s*,\\s*"));

            if (id != null) {
                Product product = new Product(id, title, price, oldPrice, description, selectedCategoryId, sizes, imageUrl, isHot);
                databaseReference.child(Constants.KEY_COLLECTION_PRODUCTS).child(id).setValue(product)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CreateProductActivity.this, "Product saved", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(CreateProductActivity.this, "Failed to save product", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).substring(uri.getLastPathSegment().lastIndexOf('.') + 1);
    }
}

