package com.itscool.smartschool.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.itscool.smartschool.R;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private void pushNotification(Intent intent,String title, String message) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "alert_001")

                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(pendingIntent)
                .setOngoing(false)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(false);

        if(Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {
            mBuilder.setSmallIcon(R.drawable.notification_logo_trans);
            mBuilder.setColorized(true);
            mBuilder.setColor(Color.parseColor("#f38000"));
            Log.e("MANUFACTURER", Build.MANUFACTURER);
        } else {
            mBuilder.setSmallIcon(R.drawable.notification_logo);
        }

        if (mNotificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build();

                NotificationChannel channel = new NotificationChannel("alert_001",
                        "Alert",
                        NotificationManager.IMPORTANCE_HIGH);


                channel.setSound(defaultSoundUri, attributes);

                mNotificationManager.createNotificationChannel(channel);
                mBuilder.setChannelId(channel.getId());
            }
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, "Status: Message Received");
        if (remoteMessage != null)
        {
            Log.e(TAG, "Data: "+remoteMessage.getData().toString());
            Map<String,String> data = remoteMessage.getData();
            Log.e("data", data.toString());
            String mTitle = data.get("title");
            String mMessage = data.get("body");
            String action = data.get("action");

            Intent intent;

            try {
                switch (action) {
                    case "fees" :
                        intent = new Intent(this, com.itscool.smartschool.students.StudentFees.class);
                        break;

                    case "absent" :
                        intent = new Intent(this, com.itscool.smartschool.students.StudentAttendance.class);
                        break;

                    case "exam" :
                        intent = new Intent(this, com.itscool.smartschool.students.StudentExaminationList.class);
                        break;

                    case "homework" :
                        intent = new Intent(this, com.itscool.smartschool.students.StudentHomework.class);
                        break;

                    default:
                        intent = new Intent(this, com.itscool.smartschool.students.NotificationList.class);
                        break;
                }
            } catch (NullPointerException e) {
                intent = new Intent(this, com.itscool.smartschool.students.NotificationList.class);
            }

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pushNotification(intent,mTitle,mMessage);

            DatabaseHelper dataBaseHelper = new DatabaseHelper(MyFirebaseMessagingService.this);
            dataBaseHelper.insertUserDetails(mTitle,mMessage,action);
            dataBaseHelper.close();

        }
    }

}
