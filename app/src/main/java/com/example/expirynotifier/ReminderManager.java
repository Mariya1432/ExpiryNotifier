package com.example.expirynotifier;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.Locale;

public class ReminderManager {

    void startReminder(
            Context context,
            int dayOfYear,
            int reminderId,
            String uuid,
            String productName
    ) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("id", reminderId);
        intent.putExtra("uuid", uuid);
        intent.putExtra("name", productName);

        PendingIntent pendingIntent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(),
                    reminderId,
                    intent,
                    PendingIntent.FLAG_MUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(),
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }


        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);


        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);


        alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent), pendingIntent);
    }


    void stopReminder(
            Context context,
            int reminderId
    ) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);


        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(),
                    reminderId,
                    intent,
                    PendingIntent.FLAG_MUTABLE
            );
        } else {
            pendingIntent = PendingIntent.getBroadcast(
                    context.getApplicationContext(),
                    reminderId,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
        }
        alarmManager.cancel(pendingIntent);
    }
}

