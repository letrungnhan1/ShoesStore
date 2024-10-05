package com.example.shoesstore.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoesstore.Partner.EmailSender;
import com.example.shoesstore.databinding.ActivityVerifyOtpactivityBinding;
import com.example.shoesstore.utilities.Constants;

public class VerifyOTPActivity extends BaseActivity {

    private ActivityVerifyOtpactivityBinding binding;
    private int OTP;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerifyOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListener();
        Intent intent = getIntent();
        if (intent != null) {
            OTP = intent.getIntExtra("OTP", 0);
            email = intent.getStringExtra("email");
        }
    }

    private void setListener() {
        addAutoNextTextWatcher(binding.inputCode1,binding.inputCode2);
        addAutoNextTextWatcher(binding.inputCode2,binding.inputCode3);
        addAutoNextTextWatcher(binding.inputCode3,binding.inputCode4);
        addAutoNextTextWatcher(binding.inputCode4,binding.inputCode5);
        addAutoNextTextWatcher(binding.inputCode5,binding.inputCode6);
        binding.imageBack.setOnClickListener(v -> onBackPressed());

        binding.buttonVerify.setOnClickListener(v -> {
            String inputCode1 = binding.inputCode1.getText().toString();
            String inputCode2 = binding.inputCode2.getText().toString().trim();
            String inputCode3 = binding.inputCode3.getText().toString().trim();
            String inputCode4 = binding.inputCode4.getText().toString().trim();
            String inputCode5 = binding.inputCode5.getText().toString().trim();
            String inputCode6 = binding.inputCode6.getText().toString().trim();
            String code = inputCode1 + inputCode2 + inputCode3 + inputCode4 + inputCode5 +inputCode6 ;
            if (isValidOTPDetails(inputCode1,inputCode2,inputCode3,inputCode4,inputCode5,inputCode6)){
                if (code.equals(String.valueOf(OTP))){
                    //True
                    init();
                    startChangePasswordActivity(email);
                }
                else {
                    showToast("OTP is wrong");
                    init();
                }
            }
        });
        binding.textResendOTP.setOnClickListener(v -> {
            OTP = (int) (Math.random() * 900000) + 100000;
            EmailSender emailSender = new EmailSender(Constants.Sender_Email_Address, Constants.Sender_Email_Password);

            emailSender.sendEmail(OTP, email);
            showToast("OTP has been sent");
            init();
        });
    }


    private void startChangePasswordActivity(String receiverEmail) {
        Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
        intent.putExtra("email", receiverEmail);
        startActivity(intent);
        finish();
    }

    private void init(){
        binding.inputCode1.setText("");
        binding.inputCode2.setText("");
        binding.inputCode3.setText("");
        binding.inputCode4.setText("");
        binding.inputCode5.setText("");
        binding.inputCode6.setText("");
    }
    private Boolean isValidOTPDetails(String a, String b, String c, String d, String e, String f){
        if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || e.isEmpty()
                || f.isEmpty() ){
            showToast("Please enter valid code");
            return  false;
        }
        else {
            return  true;
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    //Auto action next
    private void addAutoNextTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        currentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // không cần thiết
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // không cần thiết
            }
        });
    }
}