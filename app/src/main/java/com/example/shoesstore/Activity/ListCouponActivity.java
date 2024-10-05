package com.example.shoesstore.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.shoesstore.Adapter.CouponAdapter;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityListCouponBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListCouponActivity extends AppCompatActivity {

    private ActivityListCouponBinding binding;
    private CouponAdapter couponAdapter;
    private List<Coupon> couponList;
    private DatabaseReference couponsRef;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private SecureRandom random = new SecureRandom();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListCouponBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        couponsRef = database.getReference("coupons");

        couponList = new ArrayList<>();
        couponAdapter = new CouponAdapter(couponList);
        binding.couponsRecyclerView.setAdapter(couponAdapter);

        binding.fabAddCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCoupons();
            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadActiveCoupons();
    }

    private void addCoupons() {
        for (int i = 0; i < 5; i++) {
            String code = generateRandomCode(8);
            String startDate = getCurrentDate();
            String endDate = getFutureDate(30);
            String status = "Active";
            String discount = (i + 1) * 2 + "%";  // Giảm giá từ 2% đến 10%
            Coupon coupon = new Coupon(code, startDate, endDate, status, discount);
            couponList.add(coupon);

            // Lưu coupon vào Firebase
            couponsRef.push().setValue(coupon);
        }
        couponAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Đã tạo 5 coupon mới", Toast.LENGTH_SHORT).show();
    }

    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    private String getFutureDate(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return dateFormat.format(calendar.getTime());
    }

    private void loadActiveCoupons() {
        couponsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                couponList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Coupon coupon = snapshot.getValue(Coupon.class);
                    if (coupon != null) {
                        // Kiểm tra và cập nhật trạng thái của coupon nếu cần
                        updateCouponStatus(coupon, snapshot.getRef());
                        if ("Active".equals(coupon.getStatus())) {
                            couponList.add(coupon);
                        }
                    }
                }
                couponAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ListCouponActivity.this, "Failed to load coupons.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCouponStatus(Coupon coupon, DatabaseReference couponRef) {
        try {
            Date endDate = dateFormat.parse(coupon.getEndDate());
            Date currentDate = new Date();
            if (endDate != null && endDate.before(currentDate)) {
                coupon.setStatus("Inactive");
                couponRef.child("status").setValue("Inactive");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}