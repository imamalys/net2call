package com.atlasat.android_phone_sdk.utils;

import com.atlasat.android_phone_sdk.SDKCore;

import org.linphone.core.Account;
import org.linphone.core.Address;

public class Utils {
    public static String getDisplayName(Address address) {
        if (address == null) return "[null]";

        if (address.getDisplayName() == null) {
            for (Account account : SDKCore.singleHolder.get().core.getAccountList()) {
                if (account.getParams().getIdentityAddress().asStringUriOnly().equals(address.asStringUriOnly())) {
                    String localDisplayName = account.getParams().getIdentityAddress().getDisplayName();
                    // Do not return an empty local display name
                    if (localDisplayName != null && !localDisplayName.isEmpty()) {
                        return localDisplayName;
                    }
                }
            }
        }

        // Do not return an empty display name
        String displayName = address.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            return displayName;
        }

        String username = address.getUsername();
        if (username != null && !username.isEmpty()) {
            return username;
        }

        return address.asString();
    }

}
