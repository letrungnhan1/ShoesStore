package com.example.shoesstore.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shoesstore.R;
import com.example.shoesstore.databinding.ActivitySignInBinding;
import com.example.shoesstore.utilities.Constants;
import com.example.shoesstore.utilities.PreferenceManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignInActivity extends BaseActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferecnceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkAndCreateUser();
        preferecnceManager = new PreferenceManager(getApplicationContext());
        //Auto SignIn
//        if(preferecnceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)){
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
        setListeners();
    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.textForgotPassword.setOnClickListener(v->
                startActivity(new Intent(getApplicationContext(), SendOTPActivity.class)));
        binding.buttonSignIn.setOnClickListener(v -> {
            if(isValidSignInDetails()){
                signIn();
            }
        });

        binding.buttonTogglePassword.setOnClickListener(v -> togglePasswordVisibility(binding.inputPassword, binding.buttonTogglePassword));
    }

    private void checkAndCreateUser() {
        DatabaseReference usersRef = database.getReference(Constants.KEY_COLLECTION_USERS);

        String name = "Admin";
        String email = "admin@gmail.com";
        String password = "123";

        usersRef.orderByChild(Constants.KEY_EMAIL).equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email đã tồn tại
//                    Toast.makeText(getApplicationContext(), "Email already exists", Toast.LENGTH_SHORT).show();
                } else {
                    // Email chưa tồn tại, tạo user mới
                    createAdmin(name, email, password, "admin");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
//                Toast.makeText(getApplicationContext(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAdmin(String name, String email, String password, String role) {
        DatabaseReference usersRef = database.getReference(Constants.KEY_COLLECTION_USERS);

        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, name);
        user.put(Constants.KEY_EMAIL, email);
        user.put(Constants.KEY_PASSWORD, password);
        user.put(Constants.KEY_ROLE, role);

        usersRef.push().setValue(user)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi tạo user thành công
//                    Toast.makeText(getApplicationContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    // Xử lý khi tạo user thất bại
//                    Toast.makeText(getApplicationContext(), "Failed to create user: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void signIn() {
        loading(true);
        DatabaseReference usersRef = database.getReference(Constants.KEY_COLLECTION_USERS);

        String inputEmail = binding.inputEmail.getText().toString();
        String inputPassword = binding.inputPassword.getText().toString();

        usersRef.orderByChild(Constants.KEY_EMAIL).equalTo(inputEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isSignedIn = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String storedPassword = userSnapshot.child(Constants.KEY_PASSWORD).getValue(String.class);
                    if (storedPassword != null && storedPassword.equals(inputPassword)) {
                        preferecnceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferecnceManager.putString(Constants.KEY_USER_ID, userSnapshot.getKey());
                        preferecnceManager.putString(Constants.KEY_NAME, userSnapshot.child(Constants.KEY_NAME).getValue(String.class));
                        preferecnceManager.putString(Constants.KEY_PASSWORD, storedPassword);
                        preferecnceManager.putString(Constants.KEY_EMAIL, inputEmail);
                        String role = userSnapshot.child(Constants.KEY_ROLE).getValue(String.class);
                        preferecnceManager.putString(Constants.KEY_ROLE, role);

                        Intent intent;
                        if ("admin".equals(role)) {
                            intent = new Intent(getApplicationContext(), DashboardActivity.class);
                        } else {
                            intent = new Intent(getApplicationContext(), MainActivity.class);
                        }

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        isSignedIn = true;
                        break;
                    }
                }
                if (!isSignedIn) {
                    loading(false);
                    showToast("Unable to sign in");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                loading(false);
                showToast(databaseError.getMessage());
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




    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    //Kiểm tra tính hợp lệ của thông tin đăng nhập nhập
    private Boolean isValidSignInDetails(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
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

        else {
            return  true;
        }
    }
}