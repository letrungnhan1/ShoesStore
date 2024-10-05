package com.example.shoesstore.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.shoesstore.Api.CreateOrder;
import com.example.shoesstore.Domain.Payment;
import com.example.shoesstore.Partner.TelegramBot;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityCheckoutBinding;
import com.example.shoesstore.utilities.Constants;
import com.example.shoesstore.utilities.PreferenceManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private String orderId;
    private DatabaseReference databaseReference;
    private PreferenceManager preferecnceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferecnceManager = new PreferenceManager(getApplicationContext());

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        init();
        loadData();
        setListeners();
    }

    private void init(){
        //Zalo pay dev
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }
    private void loadData() {
        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
        String subtotal = intent.getStringExtra("subtotal");
        String delivery = intent.getStringExtra("delivery");
        String tax = intent.getStringExtra("tax");
        String total = intent.getStringExtra("total");

        // Hiển thị dữ liệu lên các TextView tương ứng
        binding.txtSubtotal.setText(subtotal);
        binding.txtDelivery.setText(delivery);
        binding.txtTax.setText(tax);
        binding.txtTotal.setText(total);
    }

    private void setListeners() {
        binding.buttonBack.setOnClickListener(view -> onBackPressed());

        binding.btnContinue.setOnClickListener(view -> handleContinue());
    }

    private void handleContinue() {
        // Lấy ID của RadioButton đã chọn
        int selectedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId != -1) {
            // Tìm RadioButton đã được chọn
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            String selectedPaymentMethod = selectedRadioButton.getText().toString();

            // Xử lý logic khi nhấn nút "Continue" và RadioButton đã được chọn
            if (selectedRadioButtonId == R.id.rbZaloPay) {
                    CreateOrder orderApi = new CreateOrder();

                try {
                    String totalString = binding.txtTotal.getText().toString();

                    if (totalString == null || totalString.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Total is empty or null", Toast.LENGTH_LONG).show();
                        return;
                    }

                    double totalValue = Double.parseDouble(totalString.replace("$", "").trim());
                    long roundedValue = Math.round(totalValue);

    //                Toast.makeText(getApplicationContext(), "return_code: " + roundedValue, Toast.LENGTH_LONG).show();

                    JSONObject data = orderApi.createOrder(roundedValue+"");
    //                JSONObject data = orderApi.createOrder(1000+"");
                    String code = data.getString("return_code");
    //                Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();


                    if (code.equals("1")) {
                          String token = data.getString("zp_trans_token");
                        ZaloPaySDK.getInstance().payOrder(CheckoutActivity.this, token, "demozpdk://app", new PayOrderListener() {
                            @Override
                            public void onPaymentSucceeded(String s, String s1, String s2) {
                                // Lưu thông tin thanh toán vào Firebase
                                savePaymentInfo();
                                TelegramBot bot = new TelegramBot(Constants.BOT_TOKEN);
                                String message = bot.formatOrderMessage(orderId,preferecnceManager.getString(Constants.KEY_NAME), "Zalo Pay",totalString,"12");
                                bot.sendMessage("-1002014796178", message);
                                // Cập nhật trạng thái đơn hàng thành "processing"
                                updateStatusOrder();
                                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                            }

                        @Override
                        public void onPaymentCanceled(String s, String s1) {
                            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Cancel", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                            Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
               }
            }

        }

        else {
            // Nếu không có RadioButton nào được chọn
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateStatusOrder(){
        // Cập nhật trạng thái đơn hàng thành "processing"
        databaseReference.child("orders").child(orderId).child("status").setValue("pending")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Chuyển đến MainActivity sau khi cập nhật thành công
                        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý lỗi nếu cập nhật thất bại
                        Toast.makeText(getApplicationContext(), "Failed to update order status", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void savePaymentInfo() {
        // Lấy phương thức thanh toán từ RadioButton đã chọn
        int selectedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
        String paymentMethod = selectedRadioButton.getText().toString();

        // Lấy ngày giờ hiện tại
        String paymentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Tạo ID cho thanh toán
        String idPayment = databaseReference.child("payments").push().getKey();
        if (idPayment == null) return;

        // Tạo đối tượng Payment
        Payment payment = new Payment(orderId, idPayment, paymentMethod, paymentDate);

        // Lưu thông tin thanh toán vào Firebase
        databaseReference.child("payments").child(idPayment).setValue(payment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Payment recorded successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Xử lý lỗi nếu lưu thông tin thanh toán thất bại
                        Toast.makeText(getApplicationContext(), "Failed to record payment", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}