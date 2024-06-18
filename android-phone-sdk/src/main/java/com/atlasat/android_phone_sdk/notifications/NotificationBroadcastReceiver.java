package com.atlasat.android_phone_sdk.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.atlasat.android_phone_sdk.SDKCore;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.tools.Log;

public class NotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra(NotificationsManager.INTENT_NOTIF_ID, 0);
        Log.i(
                "[Notification Broadcast Receiver] Got notification broadcast for ID [" + notificationId + "]"
        );

        if (intent.getAction().equals(NotificationsManager.INTENT_ANSWER_CALL_NOTIF_ACTION) || intent.getAction().equals(NotificationsManager.INTENT_HANGUP_CALL_NOTIF_ACTION)) {
            handleCallIntent(intent);
        }
    }

    private void handleCallIntent(Intent intent) {
        String remoteSipAddress = intent.getStringExtra(NotificationsManager.INTENT_REMOTE_ADDRESS);
        if (remoteSipAddress == null) {
            Log.e("[Notification Broadcast Receiver] Remote SIP address is null for notification");
            return;
        }

        Core core = SDKCore.singleHolder.get().core;

        Address remoteAddress = core.interpretUrl(remoteSipAddress, false);
        Call call = remoteAddress != null ? core.getCallByRemoteAddress2(remoteAddress) : null;
        if (call == null) {
            Log.e("[Notification Broadcast Receiver] Couldn't find call from remote address " + remoteSipAddress);
            return;
        }

        if (intent.getAction().equals(NotificationsManager.INTENT_ANSWER_CALL_NOTIF_ACTION)) {
            SDKCore.singleHolder.get().acceptCall(call);
        } else {
            if (call.getState() == Call.State.IncomingReceived ||
                    call.getState() == Call.State.IncomingEarlyMedia) {
                SDKCore.singleHolder.get().declineCall(call);
            } else {
                SDKCore.singleHolder.get().terminateCall(call);
            }
        }
    }

}
