package com.atlasat.android_phone_sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class SIPConfigResponse implements Parcelable {
    private RegistrationSIPState state;
    private String message;

    public SIPConfigResponse(RegistrationSIPState state, String message) {
        this.state = state;
        this.message = message;
    }

    protected SIPConfigResponse(Parcel in) {
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SIPConfigResponse> CREATOR = new Creator<SIPConfigResponse>() {
        @Override
        public SIPConfigResponse createFromParcel(Parcel in) {
            return new SIPConfigResponse(in);
        }

        @Override
        public SIPConfigResponse[] newArray(int size) {
            return new SIPConfigResponse[size];
        }
    };

    public RegistrationSIPState getState() {
        return state;
    }

    public void setState(RegistrationSIPState state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
