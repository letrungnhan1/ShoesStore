package com.example.shoesstore.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ItemContainerSizeBinding;

import java.util.List;

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.SizeViewHolder> {

    private List<String> sizes;
    private OnSizeSelectedListener onSizeSelectedListener;
    private int selectedPosition = -1; // Vị trí của kích thước được chọn

    public SizeAdapter(List<String> sizes, OnSizeSelectedListener onSizeSelectedListener) {
        this.sizes = sizes;
        this.onSizeSelectedListener = onSizeSelectedListener;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemContainerSizeBinding binding = ItemContainerSizeBinding.inflate(inflater, parent, false);
        return new SizeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder holder, int position) {
        String size = sizes.get(position);
        holder.binding.sizeTextView.setText(size);

        // Cập nhật màu nền khi được chọn
        if (position == selectedPosition) {
            holder.binding.sizeTextView.setBackgroundResource(R.drawable.size_item_background_selected);
            holder.binding.sizeTextView.setTextColor(holder.binding.sizeTextView.getContext().getResources().getColor(android.R.color.white));
        } else {
            holder.binding.sizeTextView.setBackgroundResource(R.drawable.size_item_background);
            holder.binding.sizeTextView.setTextColor(holder.binding.sizeTextView.getContext().getResources().getColor(android.R.color.black));
        }

        holder.binding.sizeTextView.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                selectedPosition = currentPosition;
                notifyDataSetChanged();
                onSizeSelectedListener.onSizeSelected(size);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes.size();
    }

    public static class SizeViewHolder extends RecyclerView.ViewHolder {

        ItemContainerSizeBinding binding;

        public SizeViewHolder(@NonNull ItemContainerSizeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnSizeSelectedListener {
        void onSizeSelected(String size);
    }
}

