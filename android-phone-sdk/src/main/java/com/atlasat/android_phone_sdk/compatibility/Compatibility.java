package com.atlasat.android_phone_sdk.compatibility;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationManagerCompat;

import com.atlasat.android_phone_sdk.model.Notifiable;
import com.atlasat.android_phone_sdk.notifications.NotificationsManager;

import org.linphone.core.Call;
import org.linphone.mediastream.Version;

import java.util.Locale;

import kotlin.Suppress;

@Suppress(names = "DEPRECATION")
public class Compatibility {
    public static boolean hasPermission(Context context, String permission) {
        return Api23Compatibility.hasPermission(context, permission);
    }
    public static boolean hasPostNotificationsPermission(Context context) {
        if (Version.sdkAboveOrEqual(Version.API33_ANDROID_13_TIRAMISU)) {
            return Api33Compatibility.hasPostNotificationsPermission(context);
        } else {
            return true;
        }
    }

    public static void requestPostNotificationsPermission(Activity activity, int code) {
        if (Version.sdkAboveOrEqual(Version.API33_ANDROID_13_TIRAMISU)) {
            Api33Compatibility.requestPostNotificationsPermission(activity, code);
        }
    }

    // Method to create incoming call notification
    public static Notification createIncomingCallNotification(
            Context context,
            Call call,
            Notifiable notifiable,
            PendingIntent pendingIntent,
            NotificationsManager notificationsManager
    ) {
        String manufacturer = Build.MANUFACTURER.toLowerCase(Locale.getDefault());

        // Samsung One UI 4.0 (API 31) doesn't (currently) display CallStyle notifications well
        // Tested on Samsung S10 and Z Fold 2
        if (Version.sdkAboveOrEqual(Version.API31_ANDROID_12) && !manufacturer.equals("samsung")) {
            return Api31Compatibility.createIncomingCallNotification(
                    context,
                    call,
                    notifiable,
                    pendingIntent,
                    notificationsManager
            );
        } else if (manufacturer.equals("xiaomi")) { // Xiaomi devices don't handle CustomHeadsUpContentView correctly
            return XiaomiCompatibility.createIncomingCallNotification(
                    context,
                    call,
                    notifiable,
                    pendingIntent,
                    notificationsManager
            );
        }

        return Api26Compatibility.createIncomingCallNotification(
                context,
                call,
                notifiable,
                pendingIntent,
                notificationsManager
        );
    }

    public static int getChannelImportance(NotificationManagerCompat notificationManager, String channelId) {
        if (Version.sdkAboveOrEqual(Version.API26_O_80)) {
            return Api26Compatibility.getChannelImportance(notificationManager, channelId);
        }
        return NotificationManagerCompat.IMPORTANCE_DEFAULT;
    }

    public static void startForegroundService(Context context, Intent intent) {
        if (Version.sdkAboveOrEqual(Version.API31_ANDROID_12)) {
            Api31Compatibility.startForegroundService(context, intent);
        } else if (Version.sdkAboveOrEqual(Version.API26_O_80)) {
            Api26Compatibility.startForegroundService(context, intent);
        } else {
            Api23Compatibility.startForegroundService(context, intent);
        }
    }

    public static void createNotificationChannels(Context context, NotificationManagerCompat notificationManager) {
        if (Version.sdkAboveOrEqual(Version.API26_O_80)) {
            Api26Compatibility.createServiceChannel(context, notificationManager);
            Api26Compatibility.createIncomingCallChannel(context, notificationManager);
        }
    }

    private static void startForegroundService(Service service, int notifId, Notification notif) {
        if (Version.sdkAboveOrEqual(Version.API31_ANDROID_12)) {
            Api31Compatibility.startForegroundService(service, notifId, notif);
        } else {
            Api23Compatibility.startForegroundService(service, notifId, notif);
        }
    }


    public static void startDataSyncForegroundService(Service service, int notifId, Notification notif, boolean isCallActive) {
        if (Version.sdkAboveOrEqual(Version.API34_ANDROID_14_UPSIDE_DOWN_CAKE)) {
            Api34Compatibility.startDataSyncForegroundService(
                    service,
                    notifId,
                    notif,
                    isCallActive
            );
        } else {
            startForegroundService(service, notifId, notif);
        }
    }

    public static void startCallForegroundService(Service service, int notifId, Notification notif, boolean isCallActive) {
        if (Version.sdkAboveOrEqual(Version.API34_ANDROID_14_UPSIDE_DOWN_CAKE)) {
            Api34Compatibility.startCallForegroundService(service, notifId, notif, isCallActive);
        } else {
            startForegroundService(service, notifId, notif);
        }
    }

    public static Notification createCallNotification(Context context, Call call, Notifiable notifiable, PendingIntent pendingIntent, String channel, NotificationsManager notificationsManager) {
        String manufacturer = Build.MANUFACTURER.toLowerCase(Locale.getDefault());

        // Samsung One UI 4.0 (API 31) doesn't (currently) display CallStyle notifications well
        // Tested on Samsung S10 and Z Fold 2
        if (Version.sdkAboveOrEqual(Version.API31_ANDROID_12) && !manufacturer.equals("samsung")) {
            return Api31Compatibility.createCallNotification(context, call, notifiable, pendingIntent, channel, notificationsManager);
        } else {
            return Api26Compatibility.createCallNotification(context, call, notifiable, pendingIntent, channel, notificationsManager);
        }
    }

}
