package com.example.shoesstore.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.shoesstore.Partner.EmailSender;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivitySendOtpactivityBinding;
import com.example.shoesstore.utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class SendOTPActivity extends BaseActivity {

    private ActivitySendOtpactivityBinding binding;
    private int  otp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivitySendOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.buttonGetOTP.setOnClickListener(v->{
            if (isValidEmailDetails()){
                loading(true);
                String receiverEmail = binding.inputEmail.getText().toString().trim().toLowerCase();
                checkAvailable(receiverEmail, new OnCheckAvailableListener() {
                    @Override
                    public void onCheckAvailable(boolean isAvailable) {
                        if (isAvailable) {
                            // Email đã tồn tại
                            otp = (int) (Math.random() * 900000) + 100000;

                            EmailSender emailSender = new EmailSender(Constants.Sender_Email_Address, Constants.Sender_Email_Password);
                            // Gửi email với OTP và email người nhận
                            emailSender.sendEmail(otp, receiverEmail);

                            loading(false);
                            init();
                            startVerifyOTPActivity(otp,receiverEmail);
                        } else {
                            // Email chưa tồn tại
                            init();
                            showToast("Email doesn't exists. Please try another email!");
                            loading(false);
                        }
                    }
                });
            }
        });
    }


    private void startVerifyOTPActivity(int otp, String receiverEmail) {
        Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
        intent.putExtra("OTP", otp);
        intent.putExtra("email", receiverEmail);
        startActivity(intent);
    }

    private void checkAvailable(String email, SendOTPActivity.OnCheckAvailableListener listener) {
        DatabaseReference databaseReference = database.getReference(Constants.KEY_COLLECTION_USERS);
        databaseReference.orderByChild(Constants.KEY_EMAIL).equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean isExisted = dataSnapshot.exists();
                        listener.onCheckAvailable(isExisted);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        listener.onCheckAvailable(false);
                    }
                });
    }

    // Định nghĩa một interface để sử dụng callback
    interface OnCheckAvailableListener {
        void onCheckAvailable(boolean isAvailable);
    }

    private Boolean isValidEmailDetails(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }

        else {
            return  true;
        }
    }

    private void init(){
        binding.inputEmail.setText("");
    }


    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonGetOTP.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonGetOTP.setVisibility(View.VISIBLE);
        }
    }
}