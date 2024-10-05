package com.example.shoesstore.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shoesstore.Partner.EmailSender;
import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivitySignUpBinding;
import com.example.shoesstore.utilities.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding binding;
    private int  otp ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners() {
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonTogglePassword.setOnClickListener(v ->
                togglePasswordVisibility(binding.inputPassword, binding.buttonTogglePassword));
        binding.buttonToggleConfirmPassword.setOnClickListener(v ->
                togglePasswordVisibility(binding.inputConfirmPassword, binding.buttonToggleConfirmPassword));

        binding.buttonSignUp.setOnClickListener(v -> {
            if (isValidSignUpDetails()) {
                String receiverEmail = binding.inputEmail.getText().toString().trim().toLowerCase();
                checkAvailable(receiverEmail, new OnCheckAvailableListener() {
                    @Override
                    public void onCheckAvailable(boolean isAvailable) {
                        if (isAvailable) {
                            // Email đã tồn tại
                            init();
                            showToast("Email already exists. Please try another email!");
                        } else {
                            // Email chưa tồn tại
                            otp = (int) (Math.random() * 900000) + 100000;

                            EmailSender emailSender = new EmailSender(Constants.Sender_Email_Address, Constants.Sender_Email_Password);

                            // Gửi email với OTP và email người nhận
                            emailSender.sendEmail(otp, receiverEmail);

                            showDialog();
                        }
                    }
                });
            }
        });
    }


    private void showDialog(){
        // Create new Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setTitle("Nhập OTP");

        // Tạo một layout để chứa EditText và TextView đếm ngược
        LinearLayout layout = new LinearLayout(SignUpActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Tạo một trường nhập dữ liệu trong hộp thoại dialog
        final EditText input = new EditText(SignUpActivity.this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(input);

        // Tạo một TextView để hiển thị đếm ngược
        final TextView countDownTextView = new TextView(SignUpActivity.this);
        // Đặt khoảng cách 2dp cho TextView countDownTextView
        countDownTextView.setPadding(10, 10, 0, 0); // left, top, right, bottom
        layout.addView(countDownTextView);

        builder.setView(layout);

        // Bộ đếm ngược
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                countDownTextView.setText(seconds + "s");
            }

            public void onFinish() {
                // Khi bộ đếm kết thúc
                countDownTextView.setText("");
            }
        }.start();

        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputOTP = input.getText().toString();
                if (!countDownTextView.getText().toString().isEmpty()){
                    if (Integer.valueOf(inputOTP) == otp) {
                        signUp();
                    } else {
                        init();
                        showToast("OTP is incorrect, please enter again");
                    }
                }
                else {
                    dialog.cancel();
                    init();
                    showToast("OTP expired, please enter again");
                }
            }
        });

        builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void signUp() {
        loading(true);
        DatabaseReference usersRef = database.getReference(Constants.KEY_COLLECTION_USERS);

        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString().toLowerCase());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString().toLowerCase());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_ROLE, "customer");

        usersRef.push().setValue(user)
                .addOnSuccessListener(aVoid -> {
                    loading(false);
                    // Lưu trữ thông tin người dùng nếu cần
//                preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
//                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
//                preferenceManager.putString(Constants.KEY_NAME, binding.inputName.getText().toString());
//                preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);

                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }


    private void checkAvailable(String email, OnCheckAvailableListener listener) {
        DatabaseReference reference = database.getReference(Constants.KEY_COLLECTION_USERS);

        reference.orderByChild(Constants.KEY_EMAIL).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isExisted = dataSnapshot.exists();
                listener.onCheckAvailable(isExisted);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                listener.onCheckAvailable(false);
            }
        });
    }
    // Định nghĩa một interface để sử dụng callback
    interface OnCheckAvailableListener {
        void onCheckAvailable(boolean isAvailable);
    }

    private void togglePasswordVisibility(EditText inputPassword, Button buttonTogglePassword) {
        // Lưu vị trí con trỏ hiện tại
        int cursorPosition = inputPassword.getSelectionStart();

        if (inputPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Nếu đang  hiển thị password, chuyển sang chế độ ẩn password
            inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            // Thiết lập biểu tượng cho Button để hiển thị biểu tượng ẩn password
            buttonTogglePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0);
        } else {
            // Nếu đang ẩn password, chuyển sang chế độ hiển thị password
            inputPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            // Thiết lập biểu tượng cho Button để hiển thị biểu tượng hiển thị password
            buttonTogglePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eyeoff, 0);
        }

        // Khôi phục vị trí con trỏ
        inputPassword.setSelection(cursorPosition);
    }

    private void init(){
        binding.inputName.setText("");
        binding.inputEmail.setText("");
        binding.inputPassword.setText("");
        binding.inputConfirmPassword.setText("");
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    //Kiểm tra tính hợp lệ của thông tin
    private Boolean isValidSignUpDetails(){
        if(binding.inputName.getText().toString().trim().isEmpty()){
            showToast("Enter name");
            return false;
        }
        else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid email");
            return false;
        }
        else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }
        else if(binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("Confirm your password");
            return false;
        }
        else if(!binding.inputPassword.getText().toString().equals(binding.inputConfirmPassword.getText().toString())){
            showToast("Password and Confirm Password must be same");
            return false;
        }
        else {
            return  true;
        }
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }
}