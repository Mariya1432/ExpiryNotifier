package com.example.expirynotifier;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.expirynotifier.databinding.ActivityNotifyBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

public class NotifyActivity extends AppCompatActivity {

    private FirebaseUtils firebaseUtils;
    private ActivityNotifyBinding activityNotifyBinding;
    LocationManager manager;
    String latitude;
    String longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityNotifyBinding = ActivityNotifyBinding.inflate(getLayoutInflater());
        setContentView(activityNotifyBinding.getRoot());

        firebaseUtils = new FirebaseUtils();


        String uuid = getIntent().getStringExtra("uuid");

        firebaseUtils.getSpecificReminder(uuid).addSnapshotListener((value, error) -> {
            if (value != null) {
                ReminderClass reminderClass = value.toObject(ReminderClass.class);
                if (reminderClass != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    activityNotifyBinding.expiryDate.setText(simpleDateFormat.format(reminderClass.expiryDate));
                    if (reminderClass.imageUrl != null) {
                        Glide.with(NotifyActivity.this)
                                .load(reminderClass.imageUrl)
                                .apply(new RequestOptions().fitCenter())
                                .error(R.drawable.app_icon)
                                .into(activityNotifyBinding.image);
                    } else {
                        activityNotifyBinding.image.setVisibility(View.GONE);
                    }
                    activityNotifyBinding.itemName.setText(reminderClass.getProductName());
                    activityNotifyBinding.itemCategory.setText(reminderClass.getCategory());

                    activityNotifyBinding.delete.setOnClickListener(view -> {
                        if(reminderClass.imageUrl!=null) {
                            firebaseUtils.deleteImageWithUrl(reminderClass.imageUrl);
                        }
                        firebaseUtils.deleteReminder(reminderClass.uuid);
                        finish();
                    });

                    activityNotifyBinding.map.setOnClickListener(view -> {


                        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            OnGPS();
                        } else {
                            getLocation();
                        }


                        String cat = reminderClass.category;
                        if(cat.equals("Others")) {
                            cat = reminderClass.productName;
                        }
                        Uri gmmIntentUri = Uri.parse("geo:" +latitude + ","+longitude+"?q="+cat);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);

                    });

                }


            }
        });


    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                NotifyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                NotifyActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            Location locationGPS = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
            } else {
                Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}