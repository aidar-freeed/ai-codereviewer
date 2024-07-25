package com.adins.mss.coll.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.adins.mss.base.AppContext;
import com.adins.mss.base.GlobalData;
import com.adins.mss.base.commons.BackupManager;
import com.adins.mss.base.crashlytics.FireCrash;
import com.adins.mss.base.timeline.NewTimelineFragment;
import com.adins.mss.base.timeline.TimelineManager;
import com.adins.mss.coll.EmergencyLockActivity;
import com.adins.mss.dao.Broadcast;
import com.adins.mss.dao.User;
import com.adins.mss.foundation.camerainapp.helper.Logger;
import com.adins.mss.foundation.datatable.GetDataTableTask;
import com.adins.mss.foundation.db.dataaccess.BroadcastDataAccess;
import com.adins.mss.foundation.formatter.Tool;
import com.adins.mss.foundation.notification.Notification;
import com.adins.mss.foundation.security.storepreferences.ObscuredSharedPreferences;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

import static java.lang.Thread.NORM_PRIORITY;

/**
 * Created by Farizko on 15-Mar-17.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private String TAG = "FirebaseMessagingService";

    public static final String MAINMENU_NOTIFICATION_KEY = "MAINMENU_NOTIFICATION_KEY";
    public static final String INTENT_BROADCAST_MESSAGE = "BROADCAST_MESSAGE";
    public static final String KEY_BROADCAST_MESSAGE_ID = "broadcastId";
    public static final String KEY_BROADCAST_MESSAGE_TITLE = "title";
    public static final String KEY_BROADCAST_MESSAGE_MESSAGE = "message";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        boolean isQueryResults = false;

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
            for (Map.Entry<String, String> entry : extras.entrySet()) {
                System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
            }
            if(extras.containsKey("title")){
                if(extras.get("title").equalsIgnoreCase("releaseEmergency")){
                    if(null != EmergencyLockActivity.releaseEmergencyHandler) {
                        String[] message = extras.get("message").split(";");
                        showNotif("", message[0]);
                        Message msg = Message.obtain();
                        msg.obj = message[1];
                        msg.setTarget(EmergencyLockActivity.releaseEmergencyHandler);
                        msg.sendToTarget();
                        return;
                    }
                } else if ("query".equals(extras.get("title"))) {
                    GetDataTableTask task = new GetDataTableTask(getApplicationContext(), extras);
                    task.execute();

                    isQueryResults = true;
                }
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.d("FirebaseMessagingServ", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        String uuid = "";
        String title = "";
        String notif = "";
        try {
            notif = remoteMessage.getData().get("message");
            uuid = remoteMessage.getData().get("uuid_notification");
            String[] message = remoteMessage.getData().get("message").split("\\|");
            if (message.length > 1) {
                uuid = message[0];
                notif = message[1];
            }
            if (notif == null || notif == "") { //so try to
                title = remoteMessage.getNotification().getTitle();
                notif = remoteMessage.getNotification().getBody();
            }
        } catch (Exception e) {
            FireCrash.log(e);
            e.printStackTrace();
        }

        ObscuredSharedPreferences sharedPref = ObscuredSharedPreferences.getPrefs(getApplicationContext(),
                "GlobalData", Context.MODE_PRIVATE);
        boolean hasLogged = sharedPref.getBoolean("HAS_LOGGED", false);
        User user = GlobalData.getSharedGlobalData().getUser();
        if (null != user && hasLogged && !isQueryResults) {
            Broadcast broadcast = saveBroadcast(uuid, title, notif);
            showNotif(title, notif);
            showMessageOnDialog(broadcast);
            if (!"".equals(uuid) && null != uuid) {
                TimelineManager.insertTimelinePushNotification(getBaseContext(), uuid + "|" + notif);
            } else {
                TimelineManager.insertTimelinePushNotification(getBaseContext(), "" + notif);
            }
            //notify timeline handler to refresh
            NewTimelineFragment.getTimelineHandler().sendEmptyMessage(0);
        }
    }

    public void showNotif(String title, String notif) {
        String notifTitle = title;
        if(null == title || title.length() < 1){
            notifTitle = getApplicationContext().getString(com.adins.mss.base.R.string.push_notification);
        }

        Intent intent = new Intent(getApplicationContext(), AppContext.getInstance().getHomeClass());
        ComponentName cn = intent.getComponent();
        intent.makeMainActivity(cn);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.getSharedNotification().setDefaultIcon(
                com.adins.mss.base.R.drawable.icon_notif_new);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "messaging");
        builder.setSmallIcon(getNotificationIcon());
        builder.setContentTitle(notifTitle);
        builder.setContentText(notif);
        builder.setPriority(NORM_PRIORITY);
        NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();
        // Sets a title for the Inbox in expanded layoutInflater
        inboxStyle.setBigContentTitle(notifTitle);
        inboxStyle.bigText(notif);
        inboxStyle.setSummaryText(getApplicationContext().getString(com.adins.mss.base.R.string.click_to_open));


        builder.setDefaults(android.app.Notification.DEFAULT_ALL);
        builder.setStyle(inboxStyle);
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(
                Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, builder.build());
    }

    private void showMessageOnDialog(Broadcast broadcast) {
        Intent broadcastIntent = new Intent(INTENT_BROADCAST_MESSAGE);
        broadcastIntent.putExtra(KEY_BROADCAST_MESSAGE_ID, broadcast.getUuid_broadcast());
        broadcastIntent.putExtra(KEY_BROADCAST_MESSAGE_TITLE, broadcast.getTitle());
        broadcastIntent.putExtra(KEY_BROADCAST_MESSAGE_MESSAGE, broadcast.getMessage());
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(broadcastIntent);
    }

    private Broadcast saveBroadcast(String uuid, String title, String notif){
        Broadcast broadcast = new Broadcast();
        if (!"".equals(uuid) && null != uuid) {
            broadcast.setUuid_broadcast(uuid);
        } else {
            broadcast.setUuid_broadcast(Tool.getUUID());
        }
        broadcast.setTitle(title);
        broadcast.setMessage(notif);
        broadcast.setIs_shown(false);
        broadcast.setDtm_crt(Calendar.getInstance().getTime());
        BroadcastDataAccess.addOrUpdate(getApplicationContext(), broadcast);

        return broadcast;
    }

    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
//        return useWhiteIcon ? com.adins.mss.base.R.drawable.icon_notif_new_white : com.adins.mss.base.R.drawable.icon_notif_new;
        return com.adins.mss.base.R.drawable.icon_notif_new;
    }

}
