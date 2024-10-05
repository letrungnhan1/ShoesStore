package com.example.shoesstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesstore.Domain.Order;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ItemCompletedContainerBinding;

import java.util.List;

public class CompletedOrderAdapter extends RecyclerView.Adapter<CompletedOrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public CompletedOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCompletedContainerBinding binding = ItemCompletedContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.orderId.setText(order.getOrderId());
        holder.binding.txtAddress.setText(order.getAddress());
        holder.binding.itemPrice.setText("Total: " + order.getTotalAmount() + "vnd");
        // Set image if any
        // Picasso.get().load(order.getImageUrl()).into(holder.binding.itemImage);
        holder.binding.itemImage.setImageResource(R.drawable.logo2);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemCompletedContainerBinding binding;

        public OrderViewHolder(@NonNull ItemCompletedContainerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
