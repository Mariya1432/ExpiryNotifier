package com.example.expirynotifier;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.expirynotifier.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding profileBinding;
    private FirebaseUtils firebaseUtils;
    private Uri profileImageUri = null;
    private ProgressHandler progressHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());
        setSupportActionBar(profileBinding.toolbar);
        progressHandler = new ProgressHandler(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firebaseUtils = new FirebaseUtils();

        addMenu();


        profileBinding.logout.setOnClickListener(v -> {
            firebaseUtils.logout();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        profileBinding.profileImage.setOnClickListener(v -> selectImage());

        if (firebaseUtils.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(firebaseUtils.getCurrentUser().getPhotoUrl())
                    .apply(new RequestOptions().fitCenter())
                    .placeholder(R.drawable.profile_icon)
                    .error(R.drawable.profile_icon)
                    .into(profileBinding.profileImage);
        }
        if (firebaseUtils.getCurrentUser().getDisplayName() != null) {
            profileBinding.profileNameET.setText(firebaseUtils.getCurrentUser().getDisplayName());
        }

        profileBinding.save.setOnClickListener(v -> {
            progressHandler.show();
            if (profileImageUri != null) {
                firebaseUtils.uploadImage(profileImageUri).addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        firebaseUtils.getProfileStorageReference().getDownloadUrl().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                String profileUri = task1.getResult().toString();
                                firebaseUtils.updateProfile(
                                        profileBinding.profileNameET.getText().toString(), profileUri).addOnCompleteListener(task11 -> {
                                    if (task11.isSuccessful()) {
                                        progressHandler.hide();
                                        Toast.makeText(
                                                ProfileActivity.this, "Profile Saved!", Toast.LENGTH_LONG).show();
                                    } else {
                                        progressHandler.hide();
                                        Toast.makeText(ProfileActivity.this, "Error :  " + task11.getException().getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                progressHandler.hide();
                                Toast.makeText(ProfileActivity.this, "Error :  " + task1.getException().getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        progressHandler.hide();
                        Toast.makeText(ProfileActivity.this, "Error :  " + task.getException().getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                String name = null;
                if(profileBinding.profileNameET.getText()!=null) {
                    name = profileBinding.profileNameET.getText().toString();
                }
                String url = null;
                if(firebaseUtils.getCurrentUser().getPhotoUrl() !=null) {
                    url = firebaseUtils.getCurrentUser().getPhotoUrl().toString();
                }
                firebaseUtils.updateProfile(name,url).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressHandler.hide();
                        Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        progressHandler.hide();
                        Toast.makeText(ProfileActivity.this, "Profile Update Failed ", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imageLauncher.launch(Intent.createChooser(intent, "Select Image"));
    }

    private final ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Glide.with(ProfileActivity.this).load(result.getData().getData())
                        .apply(new RequestOptions().fitCenter())
                        .placeholder(R.drawable.profile_icon)
                        .error(R.drawable.profile_icon)
                        .into(profileBinding.profileImage);
                profileImageUri = result.getData().getData();
            }
        }
    });


    private void addMenu() {
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
