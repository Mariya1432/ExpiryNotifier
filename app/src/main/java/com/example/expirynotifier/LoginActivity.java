package com.example.expirynotifier;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expirynotifier.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding loginBinding;
    private FirebaseUtils firebaseUtils;
    private ProgressHandler progressHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(loginBinding.getRoot());
        progressHandler = new ProgressHandler(this);

        firebaseUtils = new FirebaseUtils();

        loginBinding.register.setOnClickListener(view -> {
            Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(registerIntent);
        });

        loginBinding.login.setOnClickListener(view -> {
            if(loginBinding.email.getText() == null || loginBinding.email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(loginBinding.email.getText().toString()).matches()) {
                loginBinding.email.setError("Enter Valid Email ID");
            } else if(loginBinding.password.getText() == null || loginBinding.password.getText().toString().isEmpty() || loginBinding.password.getText().length() < 6){
                loginBinding.password.setError("Enter password of length greater than 6");
            } else {
                progressHandler.show();
                firebaseUtils.login(loginBinding.email.getText().toString(),loginBinding.password.getText().toString())
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                Intent registerIntent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(registerIntent);
                                finish();
                            } else {
                                progressHandler.hide();
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

    }
}