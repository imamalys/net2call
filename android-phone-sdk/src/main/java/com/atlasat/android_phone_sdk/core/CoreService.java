package com.atlasat.android_phone_sdk.core;

import android.content.Intent;
import android.util.Log;

import com.atlasat.android_phone_sdk.SDKCore;
import com.atlasat.android_phone_sdk.compatibility.Compatibility;

public class CoreService extends org.linphone.core.tools.service.CoreService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("[Service]", "Created");
        if (mServiceNotification == null) {
            mServiceNotification = SDKCore.singleHolder.required(this).notificationsManager.createServiceNotification(true);
        }
        Compatibility.startDataSyncForegroundService(this, SERVICE_NOTIF_ID, mServiceNotification, false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void createServiceNotificationChannel() {
        // Done elsewhere
    }

    @Override
    public void showForegroundServiceNotification(boolean isVideoCall) {
        Log.i("[Service]", "Starting service as foreground");
        SDKCore.singleHolder.required(this).notificationsManager.startCallForeground(this);
    }

    @Override
    public void hideForegroundServiceNotification() {
        Log.i("[Service]", "Stopping service as foreground");
        SDKCore.singleHolder.required(this).notificationsManager.stopCallForeground();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (SDKCore.singleHolder.required(this).contextExists()) {
            if (SDKCore.singleHolder.required(this).core.getCallsNb() > 0) {
                Log.w("[Service]", "Task removed but there is at least one active call, do not stop the Core!");
            } else if (!SDKCore.singleHolder.required(this).preference.keepServiceAlive()) {
                if (SDKCore.singleHolder.required(this).core.isInBackground()) {
                    Log.i("[Service]", "Task removed, stopping Core");
                    SDKCore.singleHolder.required(this).stop();
                } else {
                    Log.w("[Service]", "Task removed but Core is not in background, skipping");
                }
            } else {
                Log.i("[Service]", "Task removed but we were asked to keep the service alive, so doing nothing");
            }
        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        if (SDKCore.singleHolder.required(this).contextExists()) {
            Log.i("[Service]", "Stopping");
            SDKCore.singleHolder.required(this).notificationsManager.serviceDestroyed();
        }

        super.onDestroy();
    }
}

