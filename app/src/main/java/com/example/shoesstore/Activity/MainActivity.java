package com.example.shoesstore.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.shoesstore.Adapter.CategoryMainAdapter;
import com.example.shoesstore.Adapter.PopularAdapter;
import com.example.shoesstore.Adapter.ProductAdapter;
import com.example.shoesstore.Adapter.ProductMainAdapter;
import com.example.shoesstore.Adapter.SliderAdapter;
import com.example.shoesstore.Domain.Category;
import com.example.shoesstore.Domain.ItemsDomain;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.Domain.SliderItems;
import com.example.shoesstore.Listeners.OnProductClickListener;
import com.example.shoesstore.databinding.ActivityMainBinding;
import com.example.shoesstore.utilities.Constants;
import com.example.shoesstore.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements OnProductClickListener {

    private ActivityMainBinding binding;
    private PreferenceManager preferecnceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferecnceManager = new PreferenceManager(getApplicationContext());

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        initBanner();
        initCategory();
        initPopular();
        setListeners();
    }

    private void setListeners() {
        binding.heartIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), OrderHistoryActivity.class)));
        binding.profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                preferecnceManager.clear();
//                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
//                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });

        binding.cartIcon.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), CartActivity.class)));
    }

    private void initPopular() {
        DatabaseReference myRef = database.getReference(Constants.KEY_COLLECTION_PRODUCTS);
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<Product> products = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Product product = issue.getValue(Product.class);
                        if (product != null && product.isHot()) {
                            products.add(product);
                        }
                    }
                    if (!products.isEmpty()) {
                        binding.recyclerViewPopular.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
                        binding.recyclerViewPopular.setAdapter(new ProductMainAdapter(MainActivity.this, products, MainActivity.this));
                    }
                    binding.progressBarPopular.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.progressBarPopular.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, DetailProductActivity.class);
        intent.putExtra("product",product);
        startActivity(intent);
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference(Constants.KEY_COLLECTION_CATEGORY);
        binding.progressBarOfficial.setVisibility(View.VISIBLE);

        ArrayList<Category> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        Category item = issue.getValue(Category.class);
                        items.add(item);
                    }
                    if (!items.isEmpty()) {
                        binding.recyclerViewOfficial.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        binding.recyclerViewOfficial.setAdapter(new CategoryMainAdapter(MainActivity.this, items));
                    }
                    binding.progressBarOfficial.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banner");
        binding.progressBarBanner.setVisibility(View.VISIBLE);

        ArrayList<SliderItems> items = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        SliderItems item = issue.getValue(SliderItems.class);
                        items.add(item);
                    }
                    banners(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void banners(ArrayList<SliderItems> items) {
        binding.viewpagerSlider.setAdapter(new SliderAdapter(items, binding.viewpagerSlider));
        binding.viewpagerSlider.setClipToPadding(false);
        binding.viewpagerSlider.setClipChildren(false);
        binding.viewpagerSlider.setOffscreenPageLimit(3);
        binding.viewpagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewpagerSlider.setPageTransformer(compositePageTransformer);
    }
}