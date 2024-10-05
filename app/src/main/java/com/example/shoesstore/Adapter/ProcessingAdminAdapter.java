package com.example.shoesstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesstore.Domain.Order;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ItemProcessingAdminContainerBinding;

import java.util.List;
public class ProcessingAdminAdapter extends RecyclerView.Adapter<ProcessingAdminAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public ProcessingAdminAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProcessingAdminContainerBinding binding = ItemProcessingAdminContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.orderId.setText(order.getOrderId());
        holder.binding.txtAddress.setText(order.getAddress());
        holder.binding.itemPrice.setText("Total: " + order.getTotalAmount() + "vnd");
        holder.binding.itemImage.setImageResource(R.drawable.logo2);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemProcessingAdminContainerBinding binding;

        public OrderViewHolder(@NonNull ItemProcessingAdminContainerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
