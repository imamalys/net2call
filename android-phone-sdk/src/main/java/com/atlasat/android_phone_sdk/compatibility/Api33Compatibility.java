package com.atlasat.android_phone_sdk.compatibility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

@TargetApi(33)
public class Api33Compatibility {
    public static boolean hasPostNotificationsPermission(Context context) {
        return Compatibility.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS);
    }

    public static void requestPostNotificationsPermission(Activity activity, int code) {
        activity.requestPermissions(
                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                code
        );
    }

    public static boolean hasTelecomManagerFeature(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELECOM);
    }
}
