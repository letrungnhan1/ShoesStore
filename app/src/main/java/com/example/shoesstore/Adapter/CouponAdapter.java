package com.example.shoesstore.Adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesstore.Activity.Coupon;
import com.example.shoesstore.databinding.ItemCouponBinding;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<Coupon> couponList;

    public CouponAdapter(List<Coupon> couponList) {
        this.couponList = couponList;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCouponBinding binding = ItemCouponBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CouponViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.binding.couponCode.setText(coupon.getCode());
        holder.binding.couponStartDate.setText("Start Date: " + coupon.getStartDate());
        holder.binding.couponEndDate.setText("End Date: " + coupon.getEndDate());
        holder.binding.couponDiscount.setText("Discount: " + coupon.getDiscount());
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        ItemCouponBinding binding;

        public CouponViewHolder(@NonNull ItemCouponBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
