package com.atlasat.android_phone_sdk.model;

public class Call {
    private String callName;
    private CallState callState;

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public CallState getCallState() {
        return callState;
    }

    public void setCallState(CallState callState) {
        this.callState = callState;
    }
}
