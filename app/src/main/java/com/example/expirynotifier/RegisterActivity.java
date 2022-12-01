package com.example.expirynotifier;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expirynotifier.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding registerBinding;
    private FirebaseUtils firebaseUtils;
    private ProgressHandler progressHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(registerBinding.getRoot());
        progressHandler = new ProgressHandler(this);

        firebaseUtils = new FirebaseUtils();

        registerBinding.login.setOnClickListener(view -> finish());

        registerBinding.register.setOnClickListener(view -> {
            if (registerBinding.email.getText() == null || registerBinding.email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(registerBinding.email.getText().toString()).matches()) {
                registerBinding.email.setError("Enter Valid Email ID");
            } else if (registerBinding.password.getText() == null || registerBinding.password.getText().toString().isEmpty() || registerBinding.password.getText().length() < 6) {
                registerBinding.password.setError("Enter password of length greater than 6");
            } else {
                progressHandler.show();
                firebaseUtils.register(registerBinding.email.getText().toString(), registerBinding.password.getText().toString())
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                startActivity(registerIntent);
                                finish();
                            } else {
                                progressHandler.hide();
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}