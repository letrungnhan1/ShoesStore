package com.example.shoesstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoesstore.Activity.ListCategoryActivity;
import com.example.shoesstore.Domain.Category;
import com.example.shoesstore.databinding.ItemContainerCategoryBinding;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private Context context;
    private List<Category> categoryList;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerCategoryBinding binding = ItemContainerCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category, context);

        holder.binding.imageIcon.setOnClickListener(v -> {
            if (context instanceof ListCategoryActivity) {
                ((ListCategoryActivity) context).deleteCategory(category);
            }
        });
    }

    public void filterList(List<Category> filteredList) {
        categoryList = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ItemContainerCategoryBinding binding;

        public CategoryViewHolder(ItemContainerCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Category category, Context context) {
            binding.textName.setText(category.getTitle());
            Glide.with(context).load(category.getPicUrl()).into(binding.imageProfile);
        }
    }
}
