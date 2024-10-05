package com.example.shoesstore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.shoesstore.Adapter.ProductAdapter;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityCreateProductBinding;
import com.example.shoesstore.databinding.ActivityListProductBinding;
import com.example.shoesstore.utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListProductActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private ActivityListProductBinding binding;
    private DatabaseReference databaseReference;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, filteredProductList, this);

        binding.usersRecyclerView.setAdapter(productAdapter);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        setListeners();
        loadProducts();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.fabNewChat.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateProductActivity.class);
            startActivity(intent);
        });

        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì ở đây
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì ở đây
            }
        });
    }

    private void loadProducts() {
        binding.progressBar.setVisibility(View.VISIBLE);
        databaseReference.child(Constants.KEY_COLLECTION_PRODUCTS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                filterProducts(binding.editTextText.getText().toString());
                binding.progressBar.setVisibility(View.GONE);
                binding.usersRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.textErrorMessage.setText("Failed to load products: " + error.getMessage());
                binding.textErrorMessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filterProducts(String query) {
        filteredProductList.clear();
        if (query.isEmpty()) {
            filteredProductList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredProductList.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onEditClick(int position) {
        Product product = filteredProductList.get(position);
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("product_id", product.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(int position) {
        Product product = filteredProductList.get(position);
        databaseReference.child(Constants.KEY_COLLECTION_PRODUCTS).child(product.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.remove(product);
                        filterProducts(binding.editTextText.getText().toString());
                        Toast.makeText(ListProductActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ListProductActivity.this, "Failed to delete product", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
