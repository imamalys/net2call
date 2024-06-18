package com.atlasat.android_phone_sdk.compatibility;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.atlasat.android_phone_sdk.SDKCore;
import com.atlasat.android_phone_sdk.R;
import com.atlasat.android_phone_sdk.model.Notifiable;
import com.atlasat.android_phone_sdk.notifications.NotificationsManager;
import com.atlasat.android_phone_sdk.utils.PhoneUtils;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ConferenceInfo;
import org.linphone.core.tools.Log;


@TargetApi(26)
public class XiaomiCompatibility {
    public static Notification createIncomingCallNotification(
            Context context,
            Call call,
            Notifiable notifiable,
            PendingIntent pendingIntent,
            NotificationsManager notificationsManager
    ) {
        Bitmap roundPicture;
        String displayName;
        String address;
        String info;

        String remoteContact = call.getRemoteContact();
        Address conferenceAddress = remoteContact != null ?
                SDKCore.singleHolder.get().core.interpretUrl(remoteContact, false) : null;

        ConferenceInfo conferenceInfo = conferenceAddress != null ?
                SDKCore.singleHolder.get().core.findConferenceInformationFromUri(conferenceAddress) : null;

        displayName = conferenceInfo.getSubject() != null ? conferenceInfo.getSubject() : "Caller ID";
        address = PhoneUtils.getDisplayableAddress(conferenceInfo.getOrganizer());
        info = context.getString(R.string.incoming_call_notification_title);
        Log.i("[Notifications Manager] Displaying incoming group call notification with subject " + displayName + " and remote contact address " + remoteContact);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                context.getString(R.string.notification_channel_incoming_call_id)
        )
                .addPerson(notificationsManager.getPerson(displayName))
                .setSmallIcon(R.drawable.phone_call)
                .setContentTitle(displayName)
                .setContentText(address)
                .setSubText(info)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.primary_color))
                .setFullScreenIntent(pendingIntent, true)
                .addAction(notificationsManager.getCallDeclineAction(notifiable))
                .addAction(notificationsManager.getCallAnswerAction(notifiable));

        if (SDKCore.singleHolder.get().preference.isPreventInterfaceFromShowingUp()) {
            builder.setContentIntent(pendingIntent);
        }

        return builder.build();
    }
}

