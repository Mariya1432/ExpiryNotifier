package com.example.expirynotifier;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.expirynotifier.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements OnReminderClicked {

    private ActivityMainBinding mainBinding;
    private FirebaseUtils firebaseUtils;
    private NotifierAdapter notifierAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(mainBinding.getRoot());


        NotificationChannel channel = new NotificationChannel(
                "reminders_notification_channel_name",
                "Channel Name",
                NotificationManager.IMPORTANCE_HIGH);
        ContextCompat.getSystemService(this, NotificationManager.class)
                .createNotificationChannel(channel);


        firebaseUtils = new FirebaseUtils();
        setSupportActionBar(mainBinding.toolbar);

        this.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.profile) {
                    Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(profileIntent);
                    return true;
                }
                return false;
            }
        });

        mainBinding.floatingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
            startActivity(intent);
        });

        notifierAdapter = new NotifierAdapter(MainActivity.this, this, new NotifierAdapter.TaskDiff());
        mainBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.recyclerView.setAdapter(notifierAdapter);


    }

    @Override
    public void onReminderClicked(ReminderClass reminderClass) {
        Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
        intent.putExtra("data", reminderClass);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseUtils.getAllReminder().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                notifierAdapter.submitList(task.getResult().toObjects(ReminderClass.class));
            } else {
                Toast.makeText(MainActivity.this, "Error Fetching Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

interface OnReminderClicked {
    void onReminderClicked(ReminderClass reminderClass);
}