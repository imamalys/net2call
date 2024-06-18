package com.atlasat.android_phone_sdk.compatibility;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.ContextCompat;

import com.atlasat.android_phone_sdk.SDKCore;
import com.atlasat.android_phone_sdk.model.Notifiable;
import com.atlasat.android_phone_sdk.notifications.NotificationsManager;
import com.atlasat.android_phone_sdk.R;
import com.atlasat.android_phone_sdk.utils.PhoneUtils;

import org.linphone.core.Address;
import org.linphone.core.Call;
import org.linphone.core.ConferenceInfo;
import org.linphone.core.Friend;
import org.linphone.core.tools.Log;

@TargetApi(26)
@SuppressLint({"RemoteViewLayout", "UseCompatLoadingForDrawables"})
public class Api26Compatibility {
    public static Notification createIncomingCallNotification(
            Context context,
            Call call,
            Notifiable notifiable,
            PendingIntent pendingIntent,
            NotificationsManager notificationsManager) {

        Friend contact;
        String displayName;
        String address;
        String info;

        String remoteContact = call.getRemoteContact();
        Address conferenceAddress = remoteContact != null ? SDKCore.singleHolder.get().core.interpretUrl(remoteContact, false) : null;
        ConferenceInfo conferenceInfo = conferenceAddress != null ? SDKCore.singleHolder.get().core.findConferenceInformationFromUri(conferenceAddress) : null;

        contact = null;
        displayName = conferenceInfo.getSubject() != null ? conferenceInfo.getSubject() : "Caller ID";
        address = PhoneUtils.getDisplayableAddress(conferenceInfo.getOrganizer());
        info = context.getString(R.string.incoming_call_notification_title);
        Log.i("[Notifications Manager] Displaying incoming group call notification with subject " + displayName + " for remote contact address " + remoteContact);

        RemoteViews notificationLayoutHeadsUp = new RemoteViews(context.getPackageName(), R.layout.call_incoming_notification_heads_up);
        notificationLayoutHeadsUp.setTextViewText(R.id.caller, displayName);
        notificationLayoutHeadsUp.setTextViewText(R.id.sip_uri, address);
        notificationLayoutHeadsUp.setTextViewText(R.id.incoming_call_info, info);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context,
                context.getString(R.string.notification_channel_incoming_call_id)
        )
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .addPerson(notificationsManager.getPerson(displayName))
                .setSmallIcon(R.drawable.phone_call)
                .setContentTitle(displayName)
                .setContentText(context.getString(R.string.incoming_call_notification_title))
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
                .addAction(notificationsManager.getCallAnswerAction(notifiable))
                .setCustomHeadsUpContentView(notificationLayoutHeadsUp);

        if (SDKCore.singleHolder.get().preference.isPreventInterfaceFromShowingUp()) {
            builder.setContentIntent(pendingIntent);
        }

        return builder.build();
    }

    public static int getChannelImportance(NotificationManagerCompat notificationManager, String channelId) {
        NotificationChannel channel = notificationManager.getNotificationChannel(channelId);
        if (channel != null) {
            return channel.getImportance();
        } else {
            return NotificationManagerCompat.IMPORTANCE_NONE;
        }
    }

    public static void createServiceChannel(Context context, NotificationManagerCompat notificationManager) {
        // Create service notification channel
        String id = context.getString(R.string.notification_channel_service_id);
        String name = context.getString(R.string.notification_channel_service_name);
        String description = context.getString(R.string.notification_channel_service_name);
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
        channel.setDescription(description);
        channel.enableVibration(false);
        channel.enableLights(false);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }

    public static void createIncomingCallChannel(Context context, NotificationManagerCompat notificationManager) {
        // Create incoming calls notification channel
        String id = context.getString(R.string.notification_channel_incoming_call_id);
        String name = context.getString(R.string.notification_channel_incoming_call_name);
        String description = context.getString(R.string.notification_channel_incoming_call_name);
        NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(description);
        channel.setLightColor(context.getColor(R.color.primary_color));
        channel.enableVibration(true);
        channel.enableLights(true);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
    }

    public static void startForegroundService(Context context, Intent intent) {
        context.startForegroundService(intent);
    }

    public static Notification createCallNotification(Context context, Call call, Notifiable notifiable, PendingIntent pendingIntent, String channel, NotificationsManager notificationsManager) {
        int stringResourceId;
        int iconResourceId;
        String title;
        Person person;

        String displayName = call.getRemoteAddress().asStringUriOnly();
        title = displayName;
        person = notificationsManager.getPerson(displayName);

        switch (call.getState()) {
            case Paused:
            case Pausing:
            case PausedByRemote:
                stringResourceId = R.string.call_notification_paused;
                iconResourceId = R.drawable.topbar_call_paused_notification;
                break;
            case OutgoingRinging:
            case OutgoingProgress:
            case OutgoingInit:
            case OutgoingEarlyMedia:
                stringResourceId = R.string.call_notification_outgoing;
                iconResourceId = R.drawable.topbar_call_notification;
                break;
            default:
                stringResourceId = R.string.call_notification_active;
                iconResourceId = R.drawable.topbar_call_notification;
                break;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setContentTitle(title)
                .setContentText(context.getString(stringResourceId))
                .setSmallIcon(iconResourceId)
                .setLargeIcon(Icon.createWithAdaptiveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.user)))
                .addPerson(person)
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.primary_color))
                .addAction(notificationsManager.getCallDeclineAction(notifiable));

        builder.setContentIntent(pendingIntent);

        return builder.build();
    }

}
