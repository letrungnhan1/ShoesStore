package com.example.shoesstore.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shoesstore.Adapter.SizeAdapter;
import com.example.shoesstore.Domain.CartItem;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityDetailProductBinding;
import com.example.shoesstore.utilities.CartManager;

public class DetailProductActivity extends AppCompatActivity {

    private ActivityDetailProductBinding binding;
    private SizeAdapter sizeAdapter;
    private CartManager cartManager;
    private String selectedSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = new CartManager(this);

        Product product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            // Set product details
            Glide.with(this).load(product.getPicUrl()).into(binding.productImage);
            binding.productTitle.setText(product.getTitle());
            binding.productPrice.setText("$" + product.getPrice());

            // Set up RecyclerView for sizes
            binding.sizeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            sizeAdapter = new SizeAdapter(product.getSizes(), size -> {
                selectedSize = size; // Cập nhật kích thước được chọn
            });
            binding.sizeRecyclerView.setAdapter(sizeAdapter);

            // Handle Add to Cart button
            binding.addToCartButton.setOnClickListener(view -> {
                if (selectedSize == null) {
                    Toast.makeText(this, "Please select a size", Toast.LENGTH_SHORT).show();
                    return;
                }
                CartItem cartItem = new CartItem(product.getId(), product.getTitle(), product.getPrice(), 1, selectedSize, product.getPicUrl());
                cartManager.addToCart(cartItem);
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            });

            setListeners();
        }
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }
}