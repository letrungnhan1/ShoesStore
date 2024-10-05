package com.example.shoesstore.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityMainBinding;
import com.example.shoesstore.databinding.ActivityProfileBinding;
import com.example.shoesstore.utilities.Constants;
import com.example.shoesstore.utilities.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private PreferenceManager preferecnceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferecnceManager = new PreferenceManager(getApplicationContext());

        setListeners();
        loadData();
    }

    private void loadData() {
        String email = preferecnceManager.getString(Constants.KEY_EMAIL);
        binding.emailTxt.setText(email);
    }

    private void setListeners() {
        binding.buttonBack.setOnClickListener(view -> onBackPressed());
        binding.btnLogout.setOnClickListener(view -> {
            preferecnceManager.clear();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        });
        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
            }
        });
    }
}