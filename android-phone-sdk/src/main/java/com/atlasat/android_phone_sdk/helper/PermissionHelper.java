package com.atlasat.android_phone_sdk.helper;

import android.Manifest;
import android.content.Context;

import com.atlasat.android_phone_sdk.compatibility.Compatibility;
import com.atlasat.android_phone_sdk.utils.SingletonHolder;

import org.linphone.core.tools.Log;

public class PermissionHelper {
    private final Context context;
    public static SingletonHolder<PermissionHelper, Context> singletonHolder = new SingletonHolder<>(PermissionHelper::new);
    private PermissionHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    private boolean hasPermission(String permission) {
        boolean granted = Compatibility.hasPermission(context, permission);

        if (granted) {
            Log.d("[Permission Helper]", "Permission " + permission + " is granted");
        } else {
            Log.w("[Permission Helper]", "Permission " + permission + " is denied");
        }

        return granted;
    }


    public boolean hasPostNotificationsPermission() {
        return Compatibility.hasPostNotificationsPermission(context);
    }

    public boolean hasCameraPermission() {
        return hasPermission(Manifest.permission.CAMERA);
    }

    public boolean hasRecordAudioPermission() {
        return hasPermission(Manifest.permission.RECORD_AUDIO);
    }

}
