package com.example.shoesstore.Activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoesstore.Adapter.CategoryAdapter;
import com.example.shoesstore.Domain.Category;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityListCategoryBinding;
import com.example.shoesstore.utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListCategoryActivity extends BaseActivity {

    private ActivityListCategoryBinding binding;
    private List<Category> categoryList;
    private CategoryAdapter categoryAdapter;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private TextView textErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);

        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRecyclerView.setAdapter(categoryAdapter);

        databaseReference = database.getReference(Constants.KEY_COLLECTION_CATEGORY);

        loadCategories();
        setListeners();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.fabNewChat.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CreateCategoryActivity.class);
            startActivity(intent);
        });

        binding.editTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void filter(String text) {
        List<Category> filteredList = new ArrayList<>();
        for (Category item : categoryList) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        categoryAdapter.filterList(filteredList);
    }

    private void loadCategories() {
        progressBar.setVisibility(View.VISIBLE);
        textErrorMessage.setVisibility(View.GONE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        category.setId(dataSnapshot.getKey()); // Set the ID for each category
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                binding.usersRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                textErrorMessage.setVisibility(View.VISIBLE);
                textErrorMessage.setText(error.getMessage());
            }
        });
    }

    public void deleteCategory(Category category) {
        databaseReference.child(category.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show();
                        loadCategories(); // Reload categories after deletion
                    } else {
                        Toast.makeText(this, "Failed to delete category", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}