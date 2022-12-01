package com.example.expirynotifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;

public class LandActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        FirebaseUtils firebaseUtils = new FirebaseUtils();

        if(firebaseUtils.getCurrentUser()!=null) {
            Intent intent = new Intent(LandActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(LandActivity.this,LoginActivity.class);
            startActivity(intent);
            finish();
        }

    }
}