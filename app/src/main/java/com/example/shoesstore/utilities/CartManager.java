package com.example.shoesstore.utilities;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.shoesstore.Domain.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class CartManager {
    private static final String CART_PREFS = "CART_PREFS";
    private static final String CART_ITEMS = "CART_ITEMS";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public CartManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }

    public void addToCart(CartItem item) {
        List<CartItem> cartItems = getCartItems();
        boolean itemExists = false;
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(item.getProductId()) && cartItem.getSize().equals(item.getSize())) {
                // Nếu mục đã tồn tại, cập nhật số lượng
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                itemExists = true;
                break;
            }
        }
        if (!itemExists) {
            // Nếu mục chưa tồn tại, thêm mục mới
            cartItems.add(item);
        }
        saveCartItems(cartItems);
    }

    public void updateCartItem(CartItem item) {
        List<CartItem> cartItems = getCartItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId().equals(item.getProductId()) && cartItem.getSize().equals(item.getSize())) {
                cartItem.setQuantity(item.getQuantity());
                break;
            }
        }
        saveCartItems(cartItems);
    }

    public void removeCartItem(CartItem item) {
        List<CartItem> cartItems = getCartItems();
        cartItems.removeIf(cartItem -> cartItem.getProductId().equals(item.getProductId()) && cartItem.getSize().equals(item.getSize()));
        saveCartItems(cartItems);
    }

    public List<CartItem> getCartItems() {
        String json = sharedPreferences.getString(CART_ITEMS, null);
        Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<CartItem>();
    }

    private void saveCartItems(List<CartItem> cartItems) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(cartItems);
        editor.putString(CART_ITEMS, json);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CART_ITEMS);
        editor.apply();
    }

}