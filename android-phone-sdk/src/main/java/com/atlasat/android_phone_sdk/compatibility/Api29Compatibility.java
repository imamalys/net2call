package com.atlasat.android_phone_sdk.compatibility;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;

@TargetApi(29)
public class Api29Compatibility {
    public static boolean hasTelecomManagerPermission(Context context) {
        boolean hasReadPhoneStatePermission = Compatibility.hasPermission(context, Manifest.permission.READ_PHONE_STATE);
        boolean hasManageOwnCallsPermission = Compatibility.hasPermission(context, Manifest.permission.MANAGE_OWN_CALLS);

        return hasReadPhoneStatePermission && hasManageOwnCallsPermission;
    }
}
