package com.example.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtPwd, edtRePwd;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FirebaseAuth.getInstance().setLanguageCode("vi");
        mAuth = FirebaseAuth.getInstance();

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPwd = findViewById(R.id.edtPwd);
        edtRePwd = findViewById(R.id.edtRePwd);
        TextView btnRegist = findViewById(R.id.btnRegist);
        TextView btnSignin = findViewById(R.id.btnSignin);

        btnRegist.setOnClickListener(view -> registerUser());

        btnSignin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPwd.getText().toString().trim();
        String rePassword = edtRePwd.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

                            HashMap<String, Object> userMap = new HashMap<>();
                            userMap.put("fullName", name);
                            userMap.put("email", email);
                            userMap.put("uid", uid);
                            userMap.put("image", null);

                            mDatabase.child(uid).setValue(userMap)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Toast.makeText(this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish();
                                        } else {
                                            Log.e("Error", Objects.requireNonNull(task.getException().getMessage()));
                                            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        Log.e("Error", Objects.requireNonNull(task.getException().getMessage()));
                        Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_LONG).show();
                    }
                });
    }
}