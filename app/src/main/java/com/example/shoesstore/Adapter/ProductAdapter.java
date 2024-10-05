package com.example.shoesstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.databinding.ItemContainerProductBinding;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnProductClickListener onProductClickListener;

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener onProductClickListener) {
        this.context = context;
        this.productList = productList;
        this.onProductClickListener = onProductClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerProductBinding binding = ItemContainerProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ProductViewHolder(binding, onProductClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerProductBinding binding;
        private final OnProductClickListener onProductClickListener;

        public ProductViewHolder(ItemContainerProductBinding binding, OnProductClickListener onProductClickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.onProductClickListener = onProductClickListener;

            binding.imageEdit.setOnClickListener(v -> onProductClickListener.onEditClick(getAdapterPosition()));
            binding.imageDelete.setOnClickListener(v -> onProductClickListener.onDeleteClick(getAdapterPosition()));
        }

        public void bind(Product product) {
            binding.textName.setText(product.getTitle());
            Glide.with(binding.imageProfile.getContext())
                    .load(product.getPicUrl())
                    .into(binding.imageProfile);
        }
    }

    public interface OnProductClickListener {
        void onEditClick(int position);
        void onDeleteClick(int position);
    }
}


