package com.example.shoesstore.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoesstore.Domain.Product;
import com.example.shoesstore.Listeners.OnProductClickListener;
import com.example.shoesstore.databinding.ViewholderPopListBinding;

import java.util.List;

public class ProductMainAdapter extends RecyclerView.Adapter<ProductMainAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnProductClickListener listener;

    public ProductMainAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderPopListBinding binding = ViewholderPopListBinding.inflate(inflater, parent, false);
        return new ProductViewHolder(binding);
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

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private final ViewholderPopListBinding binding;

        public ProductViewHolder(ViewholderPopListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Xử lý sự kiện click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onProductClick(productList.get(position));
                    }
                }
            });
        }

        public void bind(Product product) {
            binding.title.setText(product.getTitle());
            binding.priceTxt.setText(String.format("$%.2f", product.getPrice()));
            binding.oldPriceTxt.setText(String.format("$%.2f", product.getOldPrice()));
            binding.oldPriceTxt.setPaintFlags(binding.oldPriceTxt.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            binding.ratingBar.setRating(product.getRating());
//            binding.reviewTxt.setText(String.valueOf(product.getReviewCount()));
//            binding.ratingTxt.setText(String.format("(%d)", product.getRatingCount()));
            Glide.with(binding.pic.getContext())
                    .load(product.getPicUrl())
                    .into(binding.pic);
        }
    }
}
