package com.example.expirynotifier;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Random;


public class AlarmReceiver extends BroadcastReceiver {


    int id;
    String uuid,name;

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) ContextCompat.getSystemService(context, NotificationManager.class);

        id = intent.getIntExtra("id", 0);
        uuid = intent.getStringExtra("uuid");
        name = intent.getStringExtra("name");

        sendReminderNotification(notificationManager,context);
    }


    void sendReminderNotification(NotificationManager notificationManager, Context applicationContext) {
        Intent contentIntent = new Intent(applicationContext, NotifyActivity.class);
        contentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contentIntent.putExtra("id", id);
        contentIntent.putExtra("uuid", uuid);
        contentIntent.putExtra("name",name);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    1,
                    contentIntent,
                    PendingIntent.FLAG_MUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getActivity(
                    applicationContext,
                    1,
                    contentIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }

        Notification builder = new NotificationCompat.Builder(applicationContext, "reminders_notification_channel_name")
                .setContentTitle("Expiry Notifier")
                .setContentText("Your Product " + name + " is about to expire")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true).build();

        Random rand = new Random();
        int id = rand.nextInt(100);
        notificationManager.notify(id, builder);
    }

}

