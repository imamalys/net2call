package com.atlasat.android_phone_sdk.utils;


import static android.telephony.TelephonyManager.NETWORK_TYPE_EDGE;
import static android.telephony.TelephonyManager.NETWORK_TYPE_GPRS;
import static android.telephony.TelephonyManager.NETWORK_TYPE_IDEN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.atlasat.android_phone_sdk.SDKCore;

import org.linphone.core.Account;
import org.linphone.core.Address;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("MissingPermission")
public class PhoneUtils {
    public static boolean checkIfNetworkHasLowBandwidth(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                switch (networkInfo.getSubtype()) {
                    case NETWORK_TYPE_EDGE:
                    case NETWORK_TYPE_GPRS:
                    case NETWORK_TYPE_IDEN:
                        return true;
                    default:
                        return false;
                }
            }
        }
        // In doubt return false
        return false;
    }

    public static String getDisplayableAddress(Address address) {
        if (address == null) return "[null]";

        if (SDKCore.singleHolder.get().preference.replaceSipUriByUsername()) {
            return address.getUsername() != null ? address.getUsername() : address.asStringUriOnly();
        } else {
            Address copy = address.clone();
            copy.clean(); // To remove gruu if any
            return copy.asStringUriOnly();
        }
    }

}
