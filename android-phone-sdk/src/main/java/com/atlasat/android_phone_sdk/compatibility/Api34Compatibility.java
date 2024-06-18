package com.atlasat.android_phone_sdk.compatibility;

import android.annotation.TargetApi;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.Notification;
import android.app.Service;
import android.content.pm.ServiceInfo;

import com.atlasat.android_phone_sdk.helper.PermissionHelper;

import org.linphone.core.tools.Log;

@TargetApi(34)
public class Api34Compatibility {
    public static void startDataSyncForegroundService(Service service, int notifId, Notification notif, boolean isCallActive) {
        int mask;
        if (isCallActive) {
            Log.i("[Api34 Compatibility]", "Trying to start service as foreground using at least FOREGROUND_SERVICE_TYPE_PHONE_CALL or FOREGROUND_SERVICE_TYPE_DATA_SYNC");
            int computeMask = ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL | ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
            if (PermissionHelper.singletonHolder.get().hasCameraPermission()) {
                Log.i("[Api34 Compatibility]", "CAMERA permission has been granted, adding FOREGROUND_SERVICE_TYPE_CAMERA");
                computeMask |= ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA;
            }
            if (PermissionHelper.singletonHolder.get().hasRecordAudioPermission()) {
                Log.i("[Api34 Compatibility]", "RECORD_AUDIO permission has been granted, adding FOREGROUND_SERVICE_TYPE_MICROPHONE");
                computeMask |= ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
            }
            mask = computeMask;
        } else {
            Log.i("[Api34 Compatibility]", "Trying to start service as foreground using only FOREGROUND_SERVICE_TYPE_DATA_SYNC because no call at the time");
            mask = ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC;
        }

        try {
            service.startForeground(notifId, notif, mask);
        } catch (ForegroundServiceStartNotAllowedException fssnae) {
            Log.e("[Api34 Compatibility]", "Can't start service as foreground!", fssnae);
        } catch (SecurityException se) {
            Log.e("[Api34 Compatibility]", "Can't start service as foreground!", se);
        } catch (Exception e) {
            Log.e("[Api34 Compatibility]", "Can't start service as foreground!", e);
        }
    }

    public static void startCallForegroundService(Service service, int notifId, Notification notif, boolean isCallActive) {
        int mask;
        if (isCallActive) {
            Log.i("[Api34 Compatibility] Trying to start service as foreground using at least FOREGROUND_SERVICE_TYPE_PHONE_CALL");
            int computeMask = ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL;
            if (PermissionHelper.singletonHolder.get().hasRecordAudioPermission()) {
                Log.i("[Api34 Compatibility] RECORD_AUDIO permission has been granted, adding FOREGROUND_SERVICE_TYPE_MICROPHONE");
                computeMask |= ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE;
            }
            mask = computeMask;
        } else {
            Log.i("[Api34 Compatibility] Trying to start service as foreground using only FOREGROUND_SERVICE_TYPE_PHONE_CALL because call isn't active yet");
            mask = ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL;
        }

        try {
            service.startForeground(notifId, notif, mask);
        } catch (Exception e) {
            Log.e("[Api34 Compatibility] Can't start service as foreground! " + e);
        }
    }
}
