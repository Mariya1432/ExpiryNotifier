package com.example.expirynotifier;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import com.example.expirynotifier.databinding.ActivityNewPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class NewPostActivity extends AppCompatActivity {

    private ActivityNewPostBinding activityNewPostBinding;
    private int selectedDate = 0;
    private Calendar expiryDate;
    private FirebaseUtils firebaseUtils;
    private Uri imageUri;
    ReminderClass existingReminderClass = null;
    ProgressHandler progressHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityNewPostBinding = ActivityNewPostBinding.inflate(getLayoutInflater());
        setContentView(activityNewPostBinding.getRoot());
        progressHandler = new ProgressHandler(this);

        firebaseUtils = new FirebaseUtils();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            existingReminderClass = getIntent().getSerializableExtra("data", ReminderClass.class);
        } else {
            existingReminderClass = (ReminderClass) getIntent().getSerializableExtra("data");
        }

        if (existingReminderClass != null) {
            activityNewPostBinding.productEdittext.setText(existingReminderClass.productName);
            String[] stringArray = getResources().getStringArray(R.array.category);
            int index = Arrays.asList(stringArray).indexOf(existingReminderClass.category);
            activityNewPostBinding.categorySpinner.setSelection(index);
            activityNewPostBinding.daysSpinner.setSelection(existingReminderClass.notifyBefore + 1);
            if (existingReminderClass.imageUrl != null) {
                Glide.with(NewPostActivity.this).load(existingReminderClass.imageUrl)
                        .placeholder(R.drawable.new_image)
                        .apply(new RequestOptions().fitCenter())
                        .error(R.drawable.new_image)
                        .into(activityNewPostBinding.image);
            }
            activityNewPostBinding.calendar.setDate(existingReminderClass.expiryDate);
            activityNewPostBinding.delete.setVisibility(View.VISIBLE);
            activityNewPostBinding.toolbar.setTitle("Edit Reminder");
        } else {
            activityNewPostBinding.delete.setVisibility(View.GONE);
            activityNewPostBinding.toolbar.setTitle("New Reminder");
        }

        activityNewPostBinding.delete.setOnClickListener(view -> {

            progressHandler.show();
            if (existingReminderClass.imageUrl != null) {
                firebaseUtils.deleteImageWithUrl(existingReminderClass.imageUrl);
            }
            firebaseUtils.deleteReminder(existingReminderClass.uuid).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    new ReminderManager().stopReminder(NewPostActivity.this, existingReminderClass.alarmId);
                    Toast.makeText(NewPostActivity.this, "Reminder Deleted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    progressHandler.hide();
                    Toast.makeText(NewPostActivity.this, "Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });

        setSupportActionBar(activityNewPostBinding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
        }

        activityNewPostBinding.calendar.setMinDate(new Date().getTime());

        activityNewPostBinding.image.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imageLauncher.launch(Intent.createChooser(intent, "Select Image"));

        });

        activityNewPostBinding.calendar.setOnDateChangeListener((calendarView, i, i1, i2) -> {
            Calendar c = Calendar.getInstance(Locale.ENGLISH);
            c.set(i, i1, i2, 0, 0, 0);
            selectedDate = c.get(Calendar.DAY_OF_YEAR);
            expiryDate = c;
        });


        addMenu();

        activityNewPostBinding.save.setOnClickListener(view -> {
            String productName = activityNewPostBinding.productEdittext.getText().toString();
            int categorySpinnerIndex = activityNewPostBinding.categorySpinner.getSelectedItemPosition();
            int daysSpinner = activityNewPostBinding.daysSpinner.getSelectedItemPosition();
            Calendar currentDate = Calendar.getInstance(Locale.ENGLISH);
            if (selectedDate == 0) {
                expiryDate = currentDate;
                selectedDate = currentDate.get(Calendar.DAY_OF_YEAR);
            }
            int dateToRemind = selectedDate - daysSpinner + 1;
            currentDate.set(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE), 0, 0, 1);

            if (productName.isEmpty()) {
                Toast.makeText(NewPostActivity.this, "Product Name Cannot be empty", Toast.LENGTH_SHORT).show();
            } else if (categorySpinnerIndex == 0) {
                Toast.makeText(NewPostActivity.this, "Select Category", Toast.LENGTH_SHORT).show();
            } else if (daysSpinner == 0) {
                Toast.makeText(NewPostActivity.this, "Select Days to remind", Toast.LENGTH_SHORT).show();
            } else if ((dateToRemind - 1) < currentDate.get(Calendar.DAY_OF_YEAR)) {
                Toast.makeText(NewPostActivity.this, "Already on expiry day or date passed, select another date", Toast.LENGTH_SHORT).show();
            } else if (existingReminderClass == null) {

                progressHandler.show();

                String uuid = UUID.randomUUID().toString();

                Random rand = new Random();
                int id = rand.nextInt(10000);

                if (imageUri != null) {
                    firebaseUtils.uploadReminderImage(imageUri, uuid).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            firebaseUtils.getReminderImageLocation(uuid).getDownloadUrl().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {
                                    String downloadUri = task12.getResult().toString();
                                    ReminderClass reminderClass = new ReminderClass(uuid, productName, activityNewPostBinding.categorySpinner.getSelectedItem().toString(), downloadUri, expiryDate.getTimeInMillis(), daysSpinner - 1, id);
                                    firebaseUtils.addNewReminder(reminderClass, uuid).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            new ReminderManager().startReminder(this, dateToRemind, id, uuid, productName);
                                            Toast.makeText(NewPostActivity.this, "Reminder Added Successfully", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            progressHandler.hide();
                                            Toast.makeText(NewPostActivity.this, "Error : " + task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    progressHandler.hide();
                                    Toast.makeText(NewPostActivity.this, "Error : " + task12.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            progressHandler.hide();
                            Toast.makeText(NewPostActivity.this, "Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ReminderClass reminderClass = new ReminderClass(uuid, productName, activityNewPostBinding.categorySpinner.getSelectedItem().toString(), null, expiryDate.getTimeInMillis(), daysSpinner - 1, id);
                    firebaseUtils.addNewReminder(reminderClass, uuid).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            new ReminderManager().startReminder(this, dateToRemind, id, uuid, productName);
                            Toast.makeText(NewPostActivity.this, "Reminder Added Successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            progressHandler.hide();
                            Toast.makeText(NewPostActivity.this, "Error : " + task1.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {

                Random rand = new Random();
                int id = rand.nextInt(10000);

                progressHandler.show();
                if (imageUri != null) {
                    firebaseUtils.uploadReminderImage(imageUri, existingReminderClass.getUuid()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                firebaseUtils.getReminderImageLocation(existingReminderClass.uuid).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            String downloadUri = task.getResult().toString();
                                            ReminderClass reminderClass = new ReminderClass(existingReminderClass.uuid, productName, activityNewPostBinding.categorySpinner.getSelectedItem().toString(), downloadUri, expiryDate.getTimeInMillis(), daysSpinner - 1, id);
                                            firebaseUtils.addNewReminder(reminderClass, existingReminderClass.uuid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        new ReminderManager().stopReminder(NewPostActivity.this, existingReminderClass.alarmId);
                                                        new ReminderManager().startReminder(NewPostActivity.this, dateToRemind, id, existingReminderClass.uuid, productName);
                                                        Toast.makeText(NewPostActivity.this, "Reminder Updated Successfully", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        progressHandler.hide();
                                                        Toast.makeText(NewPostActivity.this, "Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            progressHandler.hide();
                                            Toast.makeText(NewPostActivity.this, "Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                progressHandler.hide();
                                Toast.makeText(NewPostActivity.this, "Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    ReminderClass reminderClass = new ReminderClass(existingReminderClass.uuid, productName, activityNewPostBinding.categorySpinner.getSelectedItem().toString(), existingReminderClass.imageUrl, expiryDate.getTimeInMillis(), daysSpinner - 1, id);
                    firebaseUtils.addNewReminder(reminderClass, existingReminderClass.uuid).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                new ReminderManager().stopReminder(NewPostActivity.this, existingReminderClass.alarmId);
                                new ReminderManager().startReminder(NewPostActivity.this, dateToRemind, id, existingReminderClass.uuid, productName);
                                Toast.makeText(NewPostActivity.this, "Reminder Updated Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                progressHandler.hide();
                                Toast.makeText(NewPostActivity.this, "Error : " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }

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

    private final ActivityResultLauncher<Intent> imageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                Glide.with(NewPostActivity.this).load(result.getData().getData())
                        .placeholder(R.drawable.new_image)
                        .apply(new RequestOptions().fitCenter())
                        .error(R.drawable.new_image)
                        .into(activityNewPostBinding.image);
                imageUri = result.getData().getData();
            }
        }
    });
}