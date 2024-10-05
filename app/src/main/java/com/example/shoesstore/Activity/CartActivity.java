package com.example.shoesstore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.example.shoesstore.Adapter.CartItemAdapter;
import com.example.shoesstore.Domain.CartItem;
import com.example.shoesstore.Domain.Order;
import com.example.shoesstore.Domain.OrderItem;
import com.example.shoesstore.databinding.ActivityCartBinding;
import com.example.shoesstore.utilities.CartManager;
import com.example.shoesstore.utilities.Constants;
import com.example.shoesstore.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CartActivity extends AppCompatActivity implements CartItemAdapter.OnCartItemQuantityChangeListener {

    private ActivityCartBinding binding;
    private CartManager cartManager;
    private CartItemAdapter cartItemAdapter;
    private List<CartItem> cartItems;

    private PreferenceManager preferecnceManager;
    private DatabaseReference databaseReference;

    private double subtotal, delivery, tax, total;
    private double discount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        preferecnceManager = new PreferenceManager(getApplicationContext());

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        cartManager = new CartManager(this);

        cartItems = cartManager.getCartItems();

        binding.cartItems.setLayoutManager(new LinearLayoutManager(this));
        cartItemAdapter = new CartItemAdapter(this, cartItems, this);
        binding.cartItems.setAdapter(cartItemAdapter);

        binding.buttonBack.setOnClickListener(v -> onBackPressed());

        binding.buttonCheckout.setOnClickListener(v -> {
            String address = binding.etAddress.getText().toString().trim();
            if (address.isEmpty()) {
                Toast.makeText(CartActivity.this, "Please enter your address", Toast.LENGTH_SHORT).show();
            } else {
                createOrder();
            }
        });

        binding.btnCuppoon.setOnClickListener(v -> applyCoupon());

        if (!cartItems.isEmpty()) {
            updateSummary();
        }
    }

    @Override
    public void onQuantityChanged() {
        updateSummary();
    }

    private void updateSummary() {
        subtotal = calculateSubtotal();
        delivery = calculateDelivery();
        tax = calculateTax();
        total = subtotal + delivery + tax - discount;

        binding.txtSubtotal.setText(String.format("$%.2f", subtotal));
        binding.txtDelivery.setText(String.format("$%.2f", delivery));
        binding.txtTax.setText(String.format("$%.2f", tax));
        binding.txtTotal.setText(String.format("$%.2f", total));
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        for (CartItem cartItem : cartItems) {
            subtotal += cartItem.getProductPrice() * cartItem.getQuantity();
        }
        return subtotal;
    }

    private double calculateDelivery() {
        return 10.0;
    }

    private double calculateTax() {
        return subtotal * 0.01;
    }

    private void applyCoupon() {
        String couponCode = binding.edtCuppon.getText().toString().trim();
        if (TextUtils.isEmpty(couponCode)) {
            Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseReference.child("coupons").orderByChild("code").equalTo(couponCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Coupon coupon = snapshot.getValue(Coupon.class);
                        if (coupon != null && "Active".equals(coupon.getStatus())) {
                            snapshot.getRef().child("status").setValue("Inactive").addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    applyDiscount(coupon.getDiscount());
                                } else {
                                    Toast.makeText(CartActivity.this, "Failed to update coupon status", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Coupon not found or inactive", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CartActivity.this, "Failed to apply coupon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyDiscount(String discountStr) {
        if (discountStr.endsWith("%")) {
            discount = subtotal * Double.parseDouble(discountStr.replace("%", "")) / 100.0;
        } else {
            discount = Double.parseDouble(discountStr);
        }
        updateSummary();
        Toast.makeText(this, "Coupon applied successfully", Toast.LENGTH_SHORT).show();
    }

    private void createOrder() {
        String orderId = databaseReference.child("orders").push().getKey();
        if (orderId == null) return;

        String userId = preferecnceManager.getString(Constants.KEY_USER_ID);
        String address = binding.etAddress.getText().toString();
        String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        double totalAmount = total;

        Order order = new Order(orderId, userId, orderDate, "pending", totalAmount, address);

        databaseReference.child("orders").child(orderId).setValue(order)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (CartItem cartItem : cartItems) {
                            String itemId = databaseReference.child("order_items").child(orderId).push().getKey();
                            if (itemId != null) {
                                OrderItem orderItem = new OrderItem(
                                        orderId,
                                        cartItem.getProductId(),
                                        cartItem.getProductPrice(),
                                        cartItem.getQuantity(),
                                        cartItem.getSize()
                                );
                                databaseReference.child("order_items").child(orderId).child(itemId).setValue(orderItem);
                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), CheckoutActivity.class);
                        intent.putExtra("orderId", orderId);
                        intent.putExtra("subtotal", binding.txtSubtotal.getText().toString());
                        intent.putExtra("delivery", binding.txtDelivery.getText().toString());
                        intent.putExtra("tax", binding.txtTax.getText().toString());
                        intent.putExtra("total", binding.txtTotal.getText().toString());

                        cartManager.clear();
                        cartItems.clear();
                        startActivity(intent);
                    } else {
                        Toast.makeText(CartActivity.this, "Failed to create order", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}