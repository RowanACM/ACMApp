package org.rowanacm.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.us.acm.R;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import static com.google.android.gms.internal.zzs.TAG;

/**
 * Handles messages received by Firebase Messaging Service
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String LOG_TAG = MessagingService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 5000;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(LOG_TAG, "onMessageReceived() called with: " + "remoteMessage = [" + remoteMessage + "]");

        Intent intent = new Intent(this, MainTabActivity.class);
        intent.putExtra("notification_action", "unsubscribe");
        // use System.currentTimeMillis() to have a unique ID for the pending intent
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        //.addAction(null, "Unsubscribe", pIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("message")))
                        .setAutoCancel(true)
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("message"));

        Intent resultIntent;

        // Creates an explicit intent for an Activity in your app
        String openSection = remoteMessage.getData().get("section");

        Log.d(TAG, "onMessageReceived: " + openSection);

        if(openSection != null && openSection.equals("attendance")) {
            resultIntent = new Intent(this, MainTabActivity.class);

            Log.d(TAG, "onMessageReceived: ATTENDANCE");
            //resultIntent.putExtra("activity", "attendance");
        }
        else {
            resultIntent = new Intent(this, MainTabActivity.class);

            Log.d(TAG, "onMessageReceived: Main Activity");
        }


        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainTabActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }
}
