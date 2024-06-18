package com.atlasat.android_phone_sdk.notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.service.notification.StatusBarNotification;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.atlasat.android_phone_sdk.constant.AppConstant;
import com.atlasat.android_phone_sdk.helper.PermissionHelper;
import com.atlasat.android_phone_sdk.SDKCore;
import com.atlasat.android_phone_sdk.R;
import com.atlasat.android_phone_sdk.compatibility.Compatibility;
import com.atlasat.android_phone_sdk.core.CoreService;
import com.atlasat.android_phone_sdk.model.Notifiable;

import org.linphone.core.Call;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.tools.Log;

import java.util.HashMap;

public class NotificationsManager {
    public static final String INTENT_HANGUP_CALL_NOTIF_ACTION = "org.linphone.HANGUP_CALL_ACTION";
    public static final String INTENT_ANSWER_CALL_NOTIF_ACTION = "org.linphone.ANSWER_CALL_ACTION";

    public static final String INTENT_NOTIF_ID = "NOTIFICATION_ID";
    public static final String INTENT_REMOTE_ADDRESS = "REMOTE_ADDRESS";
    private Context context;
    private final NotificationManagerCompat notificationManager;
    private HashMap<String, Notifiable> callNotificationsMap = new HashMap<>();

    private Notification serviceNotification;
    private int currentForegroundServiceNotificationId = 0;
    private static int SERVICE_NOTIF_ID = 0;
    private CoreService service = null;

    @SuppressLint("ServiceCast")
    public NotificationsManager(Context context) {
        notificationManager = NotificationManagerCompat.from(context);
        this.context = context;

        Compatibility.createNotificationChannels(context, notificationManager);

        // Get the NotificationManager
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            for (StatusBarNotification notification : manager.getActiveNotifications()) {
                if (notification.getTag() == null || notification.getTag().isEmpty()) {
                    // Log and cancel existing call notifications
                    Log.w("[Notifications Manager]", "Found existing call? notification [" + notification.getId() + "], cancelling it");
                    manager.cancel(notification.getId());
                }
            }
        }
    }

    // Listener for handling call state changes
    private CoreListenerStub listener = new CoreListenerStub() {
        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
            // Log the call state change
            Log.i("[Notifications Manager] Call state changed [" + state + "]");

            // Handle different call states
            switch (call.getState()) {
                // Incoming call states
                case IncomingEarlyMedia:
                case IncomingReceived:
                    if (service != null) {
                        // Service is available, show incoming call notification
                        Log.i("[Notifications Manager] Service isn't null, show incoming call notification");
                        displayIncomingCallNotification(call, false);
                    } else {
                        // Service is not available, log a warning
                        Log.w("[Notifications Manager] No service found, waiting for it to start");
                    }
                    break;

                // Call ended or error occurred
                case End:
                case Error:
                    dismissCallNotification(call);
                    break;

                // Call released, check if it was a missed call
                case Released:
                    break;
                // Outgoing call states
                case OutgoingInit:
                case OutgoingProgress:
                case OutgoingRinging:
                    displayCallNotification(call, false);
                    break;

                // Default case, display call notification with ongoing status
                default:
                    displayCallNotification(call, true);
                    break;
            }
        }
    };

    public void onCoreReady() {
        SDKCore.singleHolder.get().core.addListener(listener);
    }

    private void dismissCallNotification(Call call) {
        String address = call.getRemoteAddress().asStringUriOnly();
        Notifiable notifiable = callNotificationsMap.get(address);
        if (notifiable != null) {
            cancel(notifiable.getNotificationId(), null);
            callNotificationsMap.remove(address);
        } else {
            Log.w("[Notifications Manager] No notification found for call " + call.getCallLog().getCallId());
        }

        // To remove microphone & camera foreground service use to foreground service if needed
        CoreService coreService = service;
        if (coreService != null && currentForegroundServiceNotificationId == SERVICE_NOTIF_ID) {
            startForeground(coreService);
        }
    }

    private void cancel(int id, @Nullable String tag) {
        Log.i("[Notifications Manager] Canceling [" + id + "] with tag [" + tag + "]");
        notificationManager.cancel(tag, id);
    }

    private Notifiable getNotifiableForCall(Call call) {
        String address = call.getRemoteAddress().asStringUriOnly();
        Notifiable notifiable = callNotificationsMap.get(address);
        if (notifiable == null) {
            notifiable = new Notifiable(getNotificationIdForCall(call));
            notifiable.setRemoteAddress(call.getRemoteAddress().asStringUriOnly());

            callNotificationsMap.put(address, notifiable);
        }
        return notifiable;
    }

    private int getNotificationIdForCall(Call call) {
        return (int) call.getCallLog().getStartDate();
    }

    public PendingIntent getCallDeclinePendingIntent(Notifiable notifiable) {
        Intent hangupIntent = new Intent(context, NotificationBroadcastReceiver.class);
        hangupIntent.setAction(INTENT_HANGUP_CALL_NOTIF_ACTION);
        hangupIntent.putExtra(INTENT_NOTIF_ID, notifiable.getNotificationId());
        hangupIntent.putExtra(INTENT_REMOTE_ADDRESS, notifiable.getRemoteAddress());

        return PendingIntent.getBroadcast(
                context,
                notifiable.getNotificationId(),
                hangupIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public PendingIntent getCallAnswerPendingIntent(Notifiable notifiable) {
        Intent answerIntent = new Intent(context, NotificationBroadcastReceiver.class);
        answerIntent.setAction(INTENT_ANSWER_CALL_NOTIF_ACTION);
        answerIntent.putExtra(INTENT_NOTIF_ID, notifiable.getNotificationId());
        answerIntent.putExtra(INTENT_REMOTE_ADDRESS, notifiable.getRemoteAddress());

        return PendingIntent.getBroadcast(
                context,
                notifiable.getNotificationId(),
                answerIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    public Person getPerson(String displayName) {
        Person.Builder builder = new Person.Builder()
                .setName(displayName)
                .setKey(displayName);

        IconCompat icon = IconCompat.createWithAdaptiveBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.user));
        builder.setIcon(icon);

        return builder.build();
    }

    public NotificationCompat.Action getCallDeclineAction(Notifiable notifiable) {
        PendingIntent declinePendingIntent = getCallDeclinePendingIntent(notifiable);

        return new NotificationCompat.Action.Builder(
                R.drawable.call_hangup,
                context.getString(R.string.incoming_call_notification_hangup_action_label),
                declinePendingIntent
        )
                .setShowsUserInterface(false)
                .build();
    }

    public NotificationCompat.Action getCallAnswerAction(Notifiable notifiable) {
        PendingIntent answerPendingIntent = getCallAnswerPendingIntent(notifiable);

        return new NotificationCompat.Action.Builder(
                R.drawable.call_audio_start,
                context.getString(R.string.incoming_call_notification_answer_action_label),
                answerPendingIntent
        ).build();
    }

    @SuppressLint("MissingPermission")
    private void notify(int id, Notification notification, String tag) {
        if (!PermissionHelper.singletonHolder.get().hasPostNotificationsPermission()) {
            Log.w("[Notifications Manager] Can't notify [" + id + "] with tag [" + tag + "], POST_NOTIFICATIONS permission isn't granted!");
            return;
        }

        Log.i("[Notifications Manager] Notifying [" + id + "] with tag [" + tag + "]");
        try {
            notificationManager.notify(tag, id, notification);
        } catch (IllegalArgumentException ignored) {

        }
    }

    public void startForegroundToKeepAppAlive(CoreService coreService) {
        startForegroundToKeepAppAlive(coreService, true);
    }

    public void startForegroundToKeepAppAlive(CoreService coreService, boolean useAutoStartDescription) {
        this.service = coreService;

        Notification notification = serviceNotification != null ? serviceNotification : createServiceNotification(useAutoStartDescription);
        if (notification == null) {
            Log.e("[Notifications Manager]", "Failed to create service notification, aborting foreground service!");
            return;
        }

        currentForegroundServiceNotificationId = SERVICE_NOTIF_ID;
        Log.i("[Notifications Manager]", "Starting service as foreground [" + currentForegroundServiceNotificationId + "]");

        Compatibility.startDataSyncForegroundService(
                coreService,
                currentForegroundServiceNotificationId,
                notification,
                false
        );
    }

    public Notification createServiceNotification(boolean useAutoStartDescription) {
        String serviceChannel = context.getString(R.string.notification_channel_service_id);
        if (Compatibility.getChannelImportance(notificationManager, serviceChannel) == NotificationManagerCompat.IMPORTANCE_NONE) {
            Log.w("[Notifications Manager]", "Service channel is disabled!");
            return null;
        }
        Intent incomingCallNotificationIntent = new Intent(context, SDKCore.singleHolder.get().clazz);
        incomingCallNotificationIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_FROM_BACKGROUND
        );
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                incomingCallNotificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, serviceChannel)
                .setContentTitle(context.getString(R.string.service_name))
                .setContentText(
                        useAutoStartDescription ?
                                context.getString(R.string.service_auto_start_description) :
                                context.getString(R.string.service_description)
                )
                .setSmallIcon(R.drawable.phone_call)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.primary_color));

        builder.setContentIntent(pendingIntent);
        Notification notif = builder.build();
        serviceNotification = notif;
        return notif;
    }

    public void stopForegroundNotificationIfPossible() {
        if (service != null && currentForegroundServiceNotificationId == SERVICE_NOTIF_ID && !SDKCore.singleHolder.get().preference.isKeepServiceAlive()) {
            Log.i("[Notifications Manager] Stopping auto-started service notification [" + currentForegroundServiceNotificationId + "]");
            stopForegroundNotification();
        }
    }

    public void stopForegroundNotification() {
        if (service != null) {
            if (currentForegroundServiceNotificationId != 0) {
                Log.i("[Notifications Manager] Stopping service as foreground [" + currentForegroundServiceNotificationId + "]");
                currentForegroundServiceNotificationId = 0;
            }
            service.stopForeground(true);
        }
    }

    public void serviceCreated(CoreService createdService) {
        Log.i("[Notifications Manager] Service has been created, keeping it around");
        service = createdService;
    }

    public void serviceDestroyed() {
        Log.i("[Notifications Manager] Service has been destroyed");
        stopForegroundNotification();
        service = null;
    }

    public void startForeground(CoreService coreService) {
        service = coreService;

        Notification notification = serviceNotification != null ? serviceNotification : createServiceNotification(false);
        if (notification == null) {
            Log.e("[Notifications Manager] Failed to create service notification, aborting foreground service!");
            return;
        }

        currentForegroundServiceNotificationId = SERVICE_NOTIF_ID;
        Log.i("[Notifications Manager] Starting service as foreground [" + currentForegroundServiceNotificationId + "]");

        boolean isActiveCall = false;

        if (SDKCore.singleHolder.get().core.getCallsNb() > 0) {
            Call currentCall = SDKCore.singleHolder.get().core.getCurrentCall() != null ? SDKCore.singleHolder.get().core.getCurrentCall() : SDKCore.singleHolder.get().core.getCalls()[0];
            switch (currentCall.getState()) {
                case IncomingReceived:
                case IncomingEarlyMedia:
                case OutgoingInit:
                case OutgoingProgress:
                case OutgoingRinging:
                    break;
                default:
                    isActiveCall = true;
                    break;
            }
        }

        Compatibility.startDataSyncForegroundService(
                coreService,
                currentForegroundServiceNotificationId,
                notification,
                isActiveCall
        );
    }

    public void destroy() {
        Log.i("[Notifications Manager] Getting destroyed, clearing foreground Service & call notifications");

        if (currentForegroundServiceNotificationId > 0) {
            Log.i("[Notifications Manager] Clearing foreground Service");
            stopForegroundNotification();
        }

        if (!callNotificationsMap.isEmpty()) {
            Log.i("[Notifications Manager] Clearing call notifications");
            for (Notifiable notifiable : callNotificationsMap.values()) {
                notificationManager.cancel(notifiable.getNotificationId());
            }
        }

        SDKCore.singleHolder.get().core.removeListener(listener);
    }

    public void startForeground() {
        String serviceChannel = context.getString(R.string.notification_channel_service_id);

        if (Compatibility.getChannelImportance(notificationManager, serviceChannel) == NotificationManagerCompat.IMPORTANCE_NONE) {
            Log.w("[Notifications Manager] Service channel is disabled!");
            return;
        }

        CoreService coreService = service;
        if (coreService != null) {
            startForeground(coreService);
        } else {
            Log.w("[Notifications Manager] Can't start service as foreground without a service, starting it now");

            Intent intent = new Intent(SDKCore.singleHolder.get().context, CoreService.class);

            try {
                Compatibility.startForegroundService(SDKCore.singleHolder.get().context, intent);
            } catch (IllegalStateException ise) {
                Log.e("[Notifications Manager] Failed to start Service: " + ise);
            } catch (SecurityException se) {
                Log.e( "[Notifications Manager] Failed to start Service: " + se);
            }
        }
    }

    public void startCallForeground(CoreService coreService) {
        service = coreService;

        if (currentForegroundServiceNotificationId != 0) {
            if (currentForegroundServiceNotificationId != SERVICE_NOTIF_ID) {
                Log.e("[Notifications Manager] There is already a foreground service notification [" + currentForegroundServiceNotificationId + "]");
            } else {
                Log.i("[Notifications Manager] There is already a foreground service notification, no need to use the call notification to keep Service alive");
            }
        } else if (SDKCore.singleHolder.get().core.getCallsNb() > 0) {
            // When this method will be called, we won't have any notification yet
            Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null ? SDKCore.singleHolder.get().core.getCurrentCall() : SDKCore.singleHolder.get().core.getCalls()[0];
            switch (call.getState()) {
                case IncomingReceived:
                case IncomingEarlyMedia:
                    Log.i("[Notifications Manager] Creating incoming call notification to be used as foreground service");
                    displayIncomingCallNotification(call, true);
                    break;
                default:
                    Log.i("[Notifications Manager] Creating call notification to be used as foreground service");
                    displayCallNotification(call, true);
                    break;
            }
        }
    }

    public void stopCallForeground() {
        if (service != null && currentForegroundServiceNotificationId != SERVICE_NOTIF_ID) {
            Log.i("[Notifications Manager] Stopping call notification [" + currentForegroundServiceNotificationId + "] used as foreground service");
            stopForegroundNotification();
        }
    }

    public void displayIncomingCallNotification(Call call, boolean useAsForeground) {
        Notifiable notifiable = getNotifiableForCall(call);
        if (notifiable.getNotificationId() == currentForegroundServiceNotificationId) {
            Log.i("[Notifications Manager] There is already a Service foreground notification for this incoming call, skipping");
            return;
        }

        try {
            int showLockScreenNotification = android.provider.Settings.Secure.getInt(
                    context.getContentResolver(),
                    "lock_screen_show_notifications",
                    0
            );
            Log.i("[Notifications Manager] Are notifications allowed on lock screen? " + (showLockScreenNotification != 0) + " (" + showLockScreenNotification + ")");
        } catch (Exception e) {
            Log.e("[Notifications Manager] Failed to get android.provider.Settings.Secure.getInt(lock_screen_show_notifications): " + e);
        }

        Intent incomingCallNotificationIntent = new Intent(context, SDKCore.singleHolder.get().clazz);
        incomingCallNotificationIntent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_USER_ACTION | Intent.FLAG_FROM_BACKGROUND
        );
        incomingCallNotificationIntent.putExtra(AppConstant.CALL_STATE, "incoming_call");
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                incomingCallNotificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = Compatibility.createIncomingCallNotification(
                context,
                call,
                notifiable,
                pendingIntent,
                this
        );
        Log.i("[Notifications Manager] Notifying incoming call notification [" + notifiable.getNotificationId() + "]");
        notify(notifiable.getNotificationId(), notification, null);

        if (useAsForeground) {
            Log.i("[Notifications Manager] Notifying incoming call notification for foreground service [" + notifiable.getNotificationId() + "]");
            startForeground(notifiable.getNotificationId(), notification, false);
        }
    }

    private void startForeground(int notificationId, Notification callNotification, boolean isCallActive) {
        CoreService coreService = service;
        if (coreService != null && (currentForegroundServiceNotificationId == 0 || currentForegroundServiceNotificationId == notificationId)) {
            Log.i("[Notifications Manager] Starting service as foreground using call notification [" + notificationId + "]");
            try {
                currentForegroundServiceNotificationId = notificationId;

                Compatibility.startCallForegroundService(
                        coreService,
                        currentForegroundServiceNotificationId,
                        callNotification,
                        isCallActive
                );
            } catch (Exception e) {
                Log.e("[Notifications Manager] Foreground service wasn't allowed! " + e);
                currentForegroundServiceNotificationId = 0;
            }
        } else {
            Log.w("[Notifications Manager] Can't start foreground service using notification id [" + notificationId + "] (current foreground service notification id is [" + currentForegroundServiceNotificationId + "]) and service [" + coreService + "]");
        }
    }

    private void displayCallNotification(Call call, boolean isCallActive) {
        Notifiable notifiable = getNotifiableForCall(call);

        String serviceChannel = context.getString(R.string.notification_channel_service_id);
        String channelToUse;
        int serviceChannelImportance = Compatibility.getChannelImportance(notificationManager, serviceChannel);
        switch (serviceChannelImportance) {
            case NotificationManagerCompat.IMPORTANCE_NONE:
                Log.w("[Notifications Manager] Service channel is disabled, using incoming call channel instead!");
                channelToUse = context.getString(R.string.notification_channel_incoming_call_id);
                break;
            case NotificationManagerCompat.IMPORTANCE_LOW:
                channelToUse = serviceChannel;
                break;
            default:
                Log.w("[Notifications Manager] Service channel importance is " + serviceChannelImportance + " and not LOW (" + NotificationManagerCompat.IMPORTANCE_LOW + ") as expected!");
                channelToUse = serviceChannel;
                break;
        }

        Intent callNotificationIntent = new Intent(context, SDKCore.singleHolder.get().clazz);
        callNotificationIntent.putExtra(AppConstant.CALL_STATE, "connected");
        callNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                callNotificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = Compatibility.createCallNotification(
                context,
                call,
                notifiable,
                pendingIntent,
                channelToUse,
                this
        );
        Log.i("[Notifications Manager] Notifying call notification [" + notifiable.getNotificationId() + "]");
        notify(notifiable.getNotificationId(), notification, null);

        CoreService coreService = service;
        if (coreService != null && (currentForegroundServiceNotificationId == 0 || currentForegroundServiceNotificationId == notifiable.getNotificationId())) {
            Log.i("[Notifications Manager] Notifying call notification for foreground service [" + notifiable.getNotificationId() + "]");
            startForeground(notifiable.getNotificationId(), notification, isCallActive);
        } else if (coreService != null && currentForegroundServiceNotificationId == SERVICE_NOTIF_ID) {
            startForeground(coreService);
        }
    }
}
