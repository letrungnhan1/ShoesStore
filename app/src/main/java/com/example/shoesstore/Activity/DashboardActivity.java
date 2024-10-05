package com.example.shoesstore.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void setListeners() {
        binding.cardOrder.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), OrderHistoryActivity.class)));
        binding.cardLogout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
            finish();
        });

        binding.cardCategory.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ListCategoryActivity.class);
            startActivity(intent);
        });

        binding.cardProduct.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), ListProductActivity.class);
            startActivity(intent);
        });

        binding.cardCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.cardCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ListCouponActivity.class);
                startActivity(intent);
            }
        });
    }
}