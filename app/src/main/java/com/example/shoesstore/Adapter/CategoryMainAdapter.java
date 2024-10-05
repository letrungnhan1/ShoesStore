package com.example.shoesstore.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoesstore.Activity.ListProductMainActivity;
import com.example.shoesstore.Domain.Category;
import com.example.shoesstore.databinding.ViewholderCategoryBinding;

import java.util.List;

public class CategoryMainAdapter extends RecyclerView.Adapter<CategoryMainAdapter.CategoryViewHolder> {

    private List<Category> categoryList;
    private Context context;

    public CategoryMainAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderCategoryBinding binding = ViewholderCategoryBinding.inflate(inflater, parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ViewholderCategoryBinding binding;

        public CategoryViewHolder(ViewholderCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Category category = categoryList.get(position);
                    Intent intent = new Intent(context, ListProductMainActivity.class);
                    intent.putExtra("category_id", category.getId());
                    context.startActivity(intent);
                }
            });
        }

        public void bind(Category category) {
            binding.title.setText(category.getTitle());
            Glide.with(binding.pic.getContext())
                    .load(category.getPicUrl())
                    .into(binding.pic);
        }
    }
}
