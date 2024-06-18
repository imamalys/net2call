package com.atlasat.net2call;

import android.app.Application;

import com.atlasat.android_phone_sdk.SDKCore;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SDKCore.singleHolder.create(this);
    }
}
