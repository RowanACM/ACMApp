package org.rowanacm.android.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.rowanacm.android.MainTabActivity;
import org.rowanacm.android.R;

/**
 * Handles messages received by Firebase Messaging Service
 */
public class MessagingService extends FirebaseMessagingService {

    public static final int NOTIFICATION_ID = 8028; // 08028 is the zip code of Glassboro

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Intent unsubscribeIntent = new Intent(this, MainTabActivity.class);
        unsubscribeIntent.putExtra("notification_action", "unsubscribe");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //.addAction(null, "Unsubscribe", intent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message")))
                        .setAutoCancel(true)
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("message"));

        Intent resultIntent = new Intent(this, MainTabActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainTabActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
