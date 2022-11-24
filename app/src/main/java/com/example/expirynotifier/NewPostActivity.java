package com.example.expirynotifier;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.expirynotifier.databinding.ActivityNewPostBinding;

public class NewPostActivity extends AppCompatActivity {

    private ActivityNewPostBinding activityNewPostBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewPostBinding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(activityNewPostBinding.getRoot());

        setSupportActionBar(activityNewPostBinding.toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }


        this.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == android.R.id.home) {
                    finish();
                    return true;
                }
                return false;
            }
        });

    }
}