package com.example.shoesstore.Domain;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shoesstore.Adapter.ProcessingAdminAdapter;
import com.example.shoesstore.Adapter.ProcessingCustomerAdapter;
import com.example.shoesstore.R;
import com.example.shoesstore.utilities.Constants;
import com.example.shoesstore.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProcessingFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Order> orderList;
    private String role;
    private PreferenceManager preferenceManager;

    public ProcessingFragment(String role) {
        this.role = role;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_processing, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = new ArrayList<>();

        preferenceManager = new PreferenceManager(getContext());
        fetchOrders();

        return view;
    }

    private void fetchOrders() {
        String userId = preferenceManager.getString(Constants.KEY_USER_ID);
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference("orders");

        ordersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    Order order = orderSnapshot.getValue(Order.class);
                    if (order != null && "processing".equals(order.getStatus())) {
                        if (role.equals("admin") || order.getUserId().equals(userId)) {
                            orderList.add(order);
                        }
                    }
                }
                if (role.equals("admin")) {
                    ProcessingAdminAdapter adapter = new ProcessingAdminAdapter(getContext(), orderList);
                    recyclerView.setAdapter(adapter);
                } else {
                    ProcessingCustomerAdapter adapter = new ProcessingCustomerAdapter(getContext(), orderList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
}