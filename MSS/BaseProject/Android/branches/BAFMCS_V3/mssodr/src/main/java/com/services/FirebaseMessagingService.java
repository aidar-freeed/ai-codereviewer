package com.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.content.IntentCompat;
import android.util.Log;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.BackupManager;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.notification.Notification;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static java.lang.Thread.NORM_PRIORITY;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationChannel channel = new NotificationChannel("messaging", "Notification", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Application Notification Message");
            mNotificationManager.createNotificationChannel(channel);
        }

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Logger.d("FirebaseMessagingServ", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.d("FirebaseMessagingServ", "Message data payload: " + remoteMessage.getData());

            //Nendi: 2019-01-18 | Add event listener
            Map<String, String> extras = remoteMessage.getData();
            if (extras.containsKey("event")) {
                String eventType = extras.get("event");
                if (eventType == null)
                    return;

                switch (eventType) {
                    case "backup":
                        String entity = extras.get("entity");
                        String constraint = null;
                        if (extras.containsKey("constraint")) {
                            constraint = extras.get("constraint");
                            BackupManager backupManager = new BackupManager(getApplicationContext());
                            backupManager.backup(entity, constraint);
                            break;
                        }
                    default:
                        break;
                }

                return;
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.d("FirebaseMessagingServ", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        String notif = null;
        try {

            notif = remoteMessage.getData().get("message");
            if (notif == null || notif == "") {
                notif = remoteMessage.getNotification().getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        User user = GlobalData.getSharedGlobalData().getUser();
        if (null != user && hasLogged){
            showNotif("" + notif);
            TimelineManager.insertTimelinePushNotification(getBaseContext(), "" + notif);
        }
    }

    public static String MAINMENU_NOTIFICATION_KEY = "MAINMENU_NOTIFICATION_KEY";
    public void showNotif(String notif) {
        String notifTitle = getApplicationContext().getString(com.adins.mss.base.R.string.push_notification);
//            String message = getApplicationContext().getString(com.adins.mss.base.R.string.push_notification_info);
        // TODO munculkan notifikasi di sini

        Intent intent = new Intent(getApplicationContext(), AppContext.getInstance().getHomeClass());
        ComponentName cn = intent.getComponent();
        Intent resultIntent = Intent.makeMainActivity(cn);
        resultIntent.setAction(MAINMENU_NOTIFICATION_KEY);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.getSharedNotification().setDefaultIcon(
                com.adins.mss.base.R.drawable.icon_notif_new);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "messaging");
        builder.setSmallIcon(getNotificationIcon());
        builder.setContentTitle(notifTitle);
        builder.setContentText(notif);
        builder.setPriority(NORM_PRIORITY);
        NotificationCompat.BigTextStyle inboxStyle =
                new NotificationCompat.BigTextStyle();
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(notifTitle);
        inboxStyle.bigText(notif);
        inboxStyle.setSummaryText(getApplicationContext().getString(com.adins.mss.base.R.string.click_to_open));


        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setStyle(inboxStyle);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, builder.build());

    }

    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? com.adins.mss.base.R.drawable.icon_notif_new_white : com.adins.mss.base.R.drawable.icon_notif_new;
    }
}
