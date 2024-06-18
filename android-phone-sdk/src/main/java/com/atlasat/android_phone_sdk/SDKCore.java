package com.atlasat.android_phone_sdk;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;

import com.atlasat.android_phone_sdk.helper.PermissionHelper;
import com.atlasat.android_phone_sdk.notifications.NotificationsManager;
import com.atlasat.android_phone_sdk.utils.PhoneUtils;
import com.atlasat.android_phone_sdk.utils.Preference;
import com.atlasat.android_phone_sdk.utils.SingletonHolder;

import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.Factory;
import org.linphone.core.tools.Log;

@SuppressLint("StaticFieldLeak")
public class SDKCore extends Application {
    public Core core;
    public Class clazz;
    public Preference preference;
    public Factory factory;
    public Context context;
    public NotificationsManager notificationsManager;
    private Handler handler = new Handler(Looper.getMainLooper());
    public static SingletonHolder<SDKCore, Context> singleHolder = new SingletonHolder<>(SDKCore::new);

    public SDKCore(Context context) {
        this.context = context.getApplicationContext();
        PermissionHelper.singletonHolder.create(context);
        factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null, null, context);
        notificationsManager = new NotificationsManager(context);
        preference = new Preference(context);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private final AudioDeviceCallback audioDeviceCallback = new AudioDeviceCallback() {
        @Override
        public void onAudioDevicesAdded(AudioDeviceInfo[] addedDevices) {
            if (addedDevices != null && addedDevices.length > 0) {
                Log.i("[Context]", "[" + addedDevices.length + "] new device(s) have been added");
                core.reloadSoundDevices();
            }
        }

        @Override
        public void onAudioDevicesRemoved(AudioDeviceInfo[] removedDevices) {
            if (removedDevices != null && removedDevices.length > 0) {
                Log.i("[Context]", "[" + removedDevices.length + "] existing device(s) have been removed");
                core.reloadSoundDevices();
            }
        }
    };

    public void start() {
        Log.i("[Context] Starting");
        Log.i("[Context] Background mode setting is enabled, starting Service");
        notificationsManager.onCoreReady();

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.registerAudioDeviceCallback(audioDeviceCallback, handler);
    }



    public void stop() {
        Log.i("[Context] Stopping");

        if (notificationsManager != null) {
            notificationsManager.destroy();
        }

        if (core != null) {
            core.stop();
        }
    }

    public boolean contextExists() {
        return core != null;
    }

    public void acceptCall(Call call) {
        Log.i("[Context] Answering call " + call);
        CallParams params = core.createCallParams(call);
        if (params == null) {
            Log.w("[Context] Answering call without params!");
            call.accept();
            return;
        }

        if (PhoneUtils.checkIfNetworkHasLowBandwidth(context)) {
            Log.w("[Context] Enabling low bandwidth mode!");
            params.setLowBandwidthEnabled(true);
        }

        call.acceptWithParams(params);
    }

    public void declineCall(Call call) {
        call.terminate();
    }

    public void terminateCall(Call call) {
        Log.i("[Context] Terminating call " + call);
        call.terminate();
    }
}
