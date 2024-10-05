package com.example.shoesstore.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivityChangePasswordBinding;
import com.example.shoesstore.utilities.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangePasswordActivity extends BaseActivity {

    private ActivityChangePasswordBinding binding;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent != null) {
            email = intent.getStringExtra("email");
        }
        setOnListeners();
    }

    private void setOnListeners() {
        binding.imageBack.setOnClickListener(v-> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            finish();
        });
        binding.buttonToggleNewPassword.setOnClickListener(v -> togglePasswordVisibility(binding.inputNewPassword, binding.buttonToggleNewPassword));
        binding.buttonToggleConfirmNewPassword.setOnClickListener(v -> togglePasswordVisibility(binding.inputConfirmNewPassword, binding.buttonToggleConfirmNewPassword));

        binding.buttonChangePassword.setOnClickListener(v -> {
            loading(true);
            if (isValidChangePasswordDetails()){
                updatePassword(binding.inputNewPassword.getText().toString().trim(),email);
            }

        });

    }

    private void updatePassword(String newPassword, String yourEmail) {
        DatabaseReference databaseReference = database.getReference(Constants.KEY_COLLECTION_USERS);
        databaseReference.orderByChild("gmail").equalTo(yourEmail)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String userId = snapshot.getKey();

                                // Tạo map để cập nhật mật khẩu
                                HashMap<String, Object> updates = new HashMap<>();
                                updates.put(Constants.KEY_PASSWORD, newPassword);

                                // Cập nhật mật khẩu cho người dùng
                                databaseReference.child(userId).updateChildren(updates)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                loading(false);
                                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                                finish();
                                                Toast.makeText(getApplicationContext(), "Successfully!", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loading(false);
                                                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                                                finish();
                                                Toast.makeText(getApplicationContext(), "Password is the same as the old password.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Log.e("Error", "User with the given email does not exist.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Error", "Error getting user documents: " + databaseError.getMessage());
                    }
                });
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

    private Boolean isValidChangePasswordDetails(){

        if(binding.inputNewPassword.getText().toString().trim().isEmpty()){
            showToast("Please enter your current new password");
            return false;
        }

        else if(binding.inputConfirmNewPassword.getText().toString().trim().isEmpty()){
            showToast("Please confirm your new password");
            return false;
        }
        else if(!binding.inputNewPassword.getText().toString().equals(binding.inputConfirmNewPassword.getText().toString())){
            showToast("New password and confirm new password must be same");
            return false;
        }
        else {
            return  true;
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonChangePassword.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonChangePassword.setVisibility(View.VISIBLE);
        }
    }
}