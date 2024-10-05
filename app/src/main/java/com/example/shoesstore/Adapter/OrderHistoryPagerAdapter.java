package com.example.shoesstore.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shoesstore.Domain.CompletedFragment;
import com.example.shoesstore.Domain.PendingFragment;
import com.example.shoesstore.Domain.ProcessingFragment;

public class OrderHistoryPagerAdapter extends FragmentStateAdapter {

    private String role;

    public OrderHistoryPagerAdapter(@NonNull FragmentActivity fragmentActivity, String role) {
        super(fragmentActivity);
        this.role = role;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingFragment(role);
            case 1:
                return new ProcessingFragment(role);
            case 2:
                return new CompletedFragment(role);
            default:
                return new PendingFragment(role);
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Number of tabs
    }
}
