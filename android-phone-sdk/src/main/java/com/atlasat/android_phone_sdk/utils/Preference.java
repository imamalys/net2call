package com.atlasat.android_phone_sdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.atlasat.android_phone_sdk.SDKCore;

import org.linphone.core.Config;

@SuppressLint("StaticFieldLeak")
public class Preference {
    private Context context;
    private static volatile Preference instance;
    public Preference(Context context) {
        this.context = context;
    }

    public Config getConfig() {
        return SDKCore.singleHolder.get().core.getConfig();
    }

    public boolean redirectDeclinedCallToVoiceMail() {
        return getConfig().getBool("app", "redirect_declined_call_to_voice_mail", true);
    }

    public boolean replaceSipUriByUsername() {
        return getConfig().getBool("app", "replace_sip_uri_by_username", false);
    }

    public boolean isPreventInterfaceFromShowingUp() {
        return !getConfig().getBool("app", "keep_app_invisible", false);
    }

    public boolean keepServiceAlive() {
        return getConfig().getBool("app", "keep_service_alive", false);
    }

    public boolean isKeepServiceAlive() {
        return getConfig().getBool("app", "keep_service_alive", false);
    }
}
