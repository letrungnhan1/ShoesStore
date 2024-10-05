package com.example.shoesstore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.example.shoesstore.Adapter.ProductAdapter;
import com.example.shoesstore.Adapter.ProductMainAdapter;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.Listeners.OnProductClickListener;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityListProductMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListProductMainActivity extends AppCompatActivity implements OnProductClickListener {

    private ActivityListProductMainBinding binding;
    private DatabaseReference databaseReference;
    private List<Product> productList;
    private List<Product> filteredProductList;
    private ProductMainAdapter productAdapter;
    private String categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListProductMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        categoryId = getIntent().getStringExtra("category_id");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        productList = new ArrayList<>();
        filteredProductList = new ArrayList<>();
        productAdapter = new ProductMainAdapter(this, filteredProductList, this);

        binding.usersRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        binding.usersRecyclerView.setAdapter(productAdapter);

        binding.imageBack.setOnClickListener(v -> onBackPressed());

        loadProductsByCategory();
        setSearchFunctionality();
    }

    private void loadProductsByCategory() {
        binding.progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("Products").orderByChild("categoryId").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        productList.add(product);
                    }
                }
                filteredProductList.addAll(productList);
                productAdapter.notifyDataSetChanged();
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

    private void setSearchFunctionality() {
        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
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
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, DetailProductActivity.class);
        intent.putExtra("product",product); // truyền sản phẩm sang HomeActivity nếu cần
        startActivity(intent);
    }
}
