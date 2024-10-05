package com.example.shoesstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoesstore.Domain.Order;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ItemProcessingCustomerContainerBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
public class ProcessingCustomerAdapter extends RecyclerView.Adapter<ProcessingCustomerAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public ProcessingCustomerAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProcessingCustomerContainerBinding binding = ItemProcessingCustomerContainerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.binding.orderId.setText(order.getOrderId());
        holder.binding.txtAddress.setText(order.getAddress());
        holder.binding.itemPrice.setText("Total: " + order.getTotalAmount() + "vnd");

        holder.binding.itemImage.setImageResource(R.drawable.logo2);
        holder.binding.receivedButton.setOnClickListener(v -> {
            // Update order status to "completed"
            DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders").child(order.getOrderId());
            ordersRef.child("status").setValue("completed");
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ItemProcessingCustomerContainerBinding binding;

        public OrderViewHolder(@NonNull ItemProcessingCustomerContainerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
