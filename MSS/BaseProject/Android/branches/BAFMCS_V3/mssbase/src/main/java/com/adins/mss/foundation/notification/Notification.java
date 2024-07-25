package com.adins.mss.foundation.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.AudioManager;
import android.media.RingtoneManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;

import java.util.Date;


/**
 * @author bong.rk
 */
public class Notification {
    private static Notification sharedNotification;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    /**
     * This one must be set before running showNotification method
     */
    private static int defaultIcon;

    /**
     * Contructor to create object Notification
     */
    private Notification() {

    }

    /**
     * create or get sharedNotification as a singleton of this class
     *
     * @return static sharedNotification
     */
    public static Notification getSharedNotification() {
        if (sharedNotification == null)
            sharedNotification = new Notification();
        return sharedNotification;
    }

    /**
     * To get static defaultIcon which has been set
     *
     * @return defaultIcon - int data type contain value of R.drawable which has been set
     */
    private static int getDefaultIcon() {
        return defaultIcon;
    }

    /**
     * This defaultIcon must be set in order to make a notification
     *
     * @param defaultIcon - int data type contain value of R.drawable.
     */
    public void setDefaultIcon(int defaultIcon) {
        Notification.defaultIcon = defaultIcon;
    }

    /**
     * Create and show notification on notification bar with no vibrate, no sound and no auto cancel notif.
     * auto cancel notif is a method to clear notification after it is tapped.
     * This method will set vibrate and autocancel to false
     *
     * @param context       - application context from activity class.
     * @param notifTitle    - a String text to be a title of notification.
     * @param message       - a String text to be a message of notification.
     * @param notifId       - int data type as an identifier of a notification.
     *                      <br>The new one will replace the old one if both of identifiers are same.
     *                      <br>
     * @param pendingIntent - pending intent of notification to run activity after notification is tapped.
     *                      <br>Pending intent could contain more than one intent i.e. (bacwardintent, resultintent).
     */
    public void showNotification(Context context, String notifTitle,
                                 String message, int notifId, PendingIntent pendingIntent) {
        showNotification(context, notifTitle, message, notifId, Vibrate.OFF, Tone.OFF, false, pendingIntent);
    }

    /**
     * Create and show notification on notification bar with vibrate, sound and auto cancel notif.
     * <br>Auto cancel notif is a method to clear notification after it is tapped.
     * <br>
     *
     * @param context        - application context from activity class.
     * @param notifTitle     - a String text to be a title of notification.
     * @param message        - a String text to be a message of notification.
     * @param notifId        - int data type as an identifier of a notification.
     *                       <br>The new one will replace the old one if both of identifiers are same.
     *                       <br>
     * @param vibrate        - enum Vibrate from this class to set kind of vibration
     * @param tone           - enum Tone from this class to set kind of sound
     * @param autoClearNotif - boolean data type to clear notification automatically after it is tapped .
     * @param pendingIntent  - pending intent of notification to run activity after notification is tapped.
     *                       <br>Pending intent could contain more than one intent i.e. (bacwardintent, resultintent).
     */
    public void showNotification(Context context, String notifTitle,
                                 String message, int notifId, Vibrate vibrate, Tone tone,
                                 boolean autoClearNotif, PendingIntent pendingIntent) {
        Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(notifTitle)
                        .setContentText(message);
        if (pendingIntent != null)
            mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(getDefaultIcon());

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        switch (vibrate) {
            case SHORT: {
                // after a 100ms delay, vibrate for 500ms
                mBuilder.setVibrate(new long[]{100, 500});
            }
            break;
            case LONG: {
                // after a 100ms delay, vibrate for 2000ms
                mBuilder.setVibrate(new long[]{100, 2000});
            }
            break;
            case TWICE: {
                // after a 100ms delay, vibrate for 200ms, pause for 500 ms and
                // then vibrate for 200ms.
                mBuilder.setVibrate(new long[]{100, 200, 500, 200});
            }
            break;
            default: {
                // no vibration
                mBuilder.setVibrate(new long[]{0});
            }
            break;
        }

        switch (tone) {
            case OFF: {
                // mute
                mBuilder.setSound(RingtoneManager.getDefaultUri(0));
                // notif.sound = Uri.parse("file:///sdcard/notification/ringer.mp3");
                // notif.sound = Uri.withAppendedPath(Audio.Media.INTERNAL_CONTENT_URI, "6");
            }
            break;
            case FORCE: {
                mBuilder.setDefaults(android.app.Notification.DEFAULT_SOUND);
                // enable vibrate and sound
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
            break;
            default:
                mBuilder.setDefaults(android.app.Notification.DEFAULT_SOUND);
                break;
        }

        mBuilder.setWhen(new Date(System.currentTimeMillis()).getTime());
        mBuilder.setTicker(message);
        mBuilder.setAutoCancel(autoClearNotif);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert nm != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            nm.createNotificationChannel(notificationChannel);
        }

        nm.notify(notifId, mBuilder.build());
    }

    /**
     * Clear a notification which has been made - based on notification id
     *
     * @param context - application context from activity class.
     * @param notifId - int data type as an identifier of a notification.
     */
    public void clearNotif(Context context, int notifId) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notifId);
    }

    /**
     * Clear all notifications which have been made
     *
     * @param context - application context from activity class.
     */
    public void clearNotifAll(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    /**
     * These values will determine kind of vibration the application would make
     * Need of permission to make a vibration on the device : Constant Value: "android.permission.VIBRATE"
     * <br>OFF = no vibration
     * <br>SHORT = short vibration fo 0.5 seconds
     * <br>LONG = long vibration for 2 seconds
     * <br>TWICE = 2 vibrations @ 0.2 seconds
     *
     * @author bong.rk
     */
    public enum Vibrate {
        OFF, SHORT, TWICE, LONG
    }

    /**
     * These values will determine kind of sound the application would make
     * <br>OFF - notification will make no sound
     * <br>NORMAL - notification will make sound when Sound in the device is enabled
     * <br>FORCE - notification will enable Sound and Vibration in the device, Vibration setting is determined by enum Vibrate
     */
    public enum Tone {
        OFF, NORMAL, FORCE
    }

}
