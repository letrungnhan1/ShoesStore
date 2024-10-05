package com.example.shoesstore.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shoesstore.Domain.CartItem;
import com.example.shoesstore.databinding.ItemContainerCartBinding;
import com.example.shoesstore.utilities.CartManager;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private OnCartItemQuantityChangeListener onCartItemQuantityChangeListener;

    public CartItemAdapter(Context context, List<CartItem> cartItems, OnCartItemQuantityChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.onCartItemQuantityChangeListener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerCartBinding binding = ItemContainerCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        holder.bind(cartItems.get(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class CartItemViewHolder extends RecyclerView.ViewHolder {

        private ItemContainerCartBinding binding;

        public CartItemViewHolder(ItemContainerCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CartItem cartItem) {
            Glide.with(context).load(cartItem.getProductImage()).into(binding.itemImage);
            binding.itemName.setText(cartItem.getProductName());
            binding.itemSize.setText(cartItem.getSize());
            binding.itemPrice.setText("$" + cartItem.getProductPrice());
            binding.itemQuantity.setText(String.valueOf(cartItem.getQuantity()));

            binding.btnIncrease.setOnClickListener(view -> {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                binding.itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
                CartManager cartManager = new CartManager(context);
                cartManager.updateCartItem(cartItem);
                if (onCartItemQuantityChangeListener != null) {
                    onCartItemQuantityChangeListener.onQuantityChanged();
                }
            });

            binding.btnDecrease.setOnClickListener(view -> {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    binding.itemQuantity.setText(String.valueOf(cartItem.getQuantity()));
                    CartManager cartManager = new CartManager(context);
                    cartManager.updateCartItem(cartItem);
                    if (onCartItemQuantityChangeListener != null) {
                        onCartItemQuantityChangeListener.onQuantityChanged();
                    }
                } else {
                    CartManager cartManager = new CartManager(context);
                    cartManager.removeCartItem(cartItem);
                    cartItems.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    if (onCartItemQuantityChangeListener != null) {
                        onCartItemQuantityChangeListener.onQuantityChanged();
                    }
                }
            });
        }
    }

    public interface OnCartItemQuantityChangeListener {
        void onQuantityChanged();
    }
}
