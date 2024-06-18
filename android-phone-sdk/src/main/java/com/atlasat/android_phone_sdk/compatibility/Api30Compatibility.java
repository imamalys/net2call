package com.atlasat.android_phone_sdk.compatibility;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;

import androidx.core.app.ActivityCompat;

@TargetApi(30)
public class Api30Compatibility {
    public static boolean hasTelecomManagerPermission(Context context) {
        return Compatibility.hasPermission(context, Manifest.permission.MANAGE_OWN_CALLS);
    }

    public static void requestTelecomManagerPermission(Activity activity, int code) {
        String[] permissions = {
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.MANAGE_OWN_CALLS
        };
        ActivityCompat.requestPermissions(activity, permissions, code);
    }
}
