package com.atlasat.android_phone_sdk.compatibility;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class Api23Compatibility {
    public static boolean hasPermission(Context context, String permission) {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static void startForegroundService(Context context, Intent intent) {
        context.startService(intent);
    }

    static void startForegroundService(Service service, int notifId, Notification notif) {
        service.startForeground(notifId, notif);
    }

}
