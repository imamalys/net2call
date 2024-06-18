package com.atlasat.android_phone_sdk.compatibility;

import android.annotation.TargetApi;
import android.app.ForegroundServiceStartNotAllowedException;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Person;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;

import androidx.core.content.ContextCompat;

import com.atlasat.android_phone_sdk.SDKCore;
import com.atlasat.android_phone_sdk.model.Notifiable;
import com.atlasat.android_phone_sdk.notifications.NotificationsManager;
import com.atlasat.android_phone_sdk.R;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ConferenceInfo;
import org.linphone.core.tools.Log;

@TargetApi(31)
public class Api31Compatibility {
    public static Notification createIncomingCallNotification(
            Context context,
            Call call,
            Notifiable notifiable,
            PendingIntent pendingIntent,
            NotificationsManager notificationsManager
    ) {
        String remoteContact = call.getRemoteContact();
        Address conferenceAddress = remoteContact != null ?
                SDKCore.singleHolder.get().core.interpretUrl(remoteContact, false) :
                null;
        ConferenceInfo conferenceInfo = conferenceAddress != null ?
                SDKCore.singleHolder.get().core.findConferenceInformationFromUri(conferenceAddress) :
                null;

        if (conferenceInfo != null) {
            Log.i("[Notifications Manager] Displaying incoming group call notification with subject " + conferenceInfo.getSubject() +
                    " and remote contact address " + remoteContact);
        } else {
            Log.i("[Notifications Manager] No conference info found for remote contact address " + remoteContact);
        }

        Person caller;
        caller = new Person.Builder()
                .setName(context.getString(R.string.incoming_call_notification_title))
                .setIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.user)))
                .setImportant(false)
                .build();

        PendingIntent declineIntent = notificationsManager.getCallDeclinePendingIntent(notifiable);
        PendingIntent answerIntent = notificationsManager.getCallAnswerPendingIntent(notifiable);

        boolean isVideoEnabledInRemoteParams = call.getRemoteParams() != null && call.getRemoteParams().isVideoEnabled();
        boolean isVideoAutomaticallyAccepted = call.getCore().getVideoActivationPolicy().getAutomaticallyAccept();
        boolean isVideo = isVideoEnabledInRemoteParams && isVideoAutomaticallyAccepted;

        Notification.Builder builder = new Notification.Builder(
                context,
                context.getString(R.string.notification_channel_incoming_call_id)
        );

        try {
            builder.setStyle(Notification.CallStyle.forIncomingCall(
                            caller,
                            declineIntent,
                            answerIntent)
                    .setIsVideo(isVideo));
        } catch (IllegalArgumentException iae) {
            Log.e("[Api31 Compatibility] Can't use notification call style: " + iae + ", using API 26 notification instead");
            return Api26Compatibility.createIncomingCallNotification(
                    context,
                    call,
                    notifiable,
                    pendingIntent,
                    notificationsManager
            );
        }

        builder.setSmallIcon(R.drawable.phone_call)
                .setCategory(Notification.CATEGORY_CALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(false)
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.primary_color))
                .setFullScreenIntent(pendingIntent, true);

        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

    public static void startForegroundService(Context context, Intent intent) {
        try {
            context.startForegroundService(intent);
        } catch (ForegroundServiceStartNotAllowedException fssnae) {
            Log.e("[Api31 Compatibility]", "Can't start service as foreground!", fssnae);
        } catch (SecurityException se) {
            Log.e("[Api31 Compatibility]", "Can't start service as foreground!", se);
        } catch (Exception e) {
            Log.e("[Api31 Compatibility]", "Can't start service as foreground!", e);
        }
    }

    public static void startForegroundService(Service service, int notifId, Notification notif) {
        try {
            service.startForeground(notifId, notif);
        } catch (ForegroundServiceStartNotAllowedException fssnae) {
            Log.e("[Api31 Compatibility] Can't start service as foreground!", fssnae);
        } catch (SecurityException se) {
            Log.e("[Api31 Compatibility] Can't start service as foreground!", se);
        } catch (Exception e) {
            Log.e("[Api31 Compatibility] Can't start service as foreground!", e);
        }
    }

    public static Notification createCallNotification(Context context, Call call, Notifiable notifiable, PendingIntent pendingIntent, String channel, NotificationsManager notificationsManager) {
        Person caller;
        caller = new Person.Builder()
                .setName(call.getRemoteAddress().asStringUriOnly())
                .setIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.phone_call)))
                .setImportant(false)
                .build();

        int iconResourceId;
        switch (call.getState()) {
            case Paused:
            case Pausing:
            case PausedByRemote:
                iconResourceId = R.drawable.topbar_call_paused_notification;
                break;
            default:
                iconResourceId = R.drawable.topbar_call_notification;
                break;
        }

        PendingIntent declineIntent = notificationsManager.getCallDeclinePendingIntent(notifiable);

        Notification.Builder builder = new Notification.Builder(context, channel)
                .setSmallIcon(iconResourceId)
                .setAutoCancel(false)
                .setCategory(Notification.CATEGORY_CALL)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.primary_color))
                .setFullScreenIntent(pendingIntent, true);

        try {
            Notification.Style style = Notification.CallStyle.forOngoingCall(caller, declineIntent)
                    .setIsVideo(false);
            builder.setStyle(style);
        } catch (IllegalArgumentException iae) {
            Log.e("[Api31 Compatibility] Can't use notification call style: " + iae + ", using API 26 notification instead");
            return Api26Compatibility.createCallNotification(context, call, notifiable, pendingIntent, channel, notificationsManager);
        }

        builder.setContentIntent(pendingIntent);
        return builder.build();
    }

}
