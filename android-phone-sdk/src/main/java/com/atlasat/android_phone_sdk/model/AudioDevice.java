package com.atlasat.android_phone_sdk.model;

import com.atlasat.android_phone_sdk.constant.AudioDeviceType;

public class AudioDevice {
    private AudioDeviceType.Type type;
    private String deviceName;
    private boolean isActive = false;

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public AudioDevice(AudioDeviceType.Type type, String deviceName) {
        this.type = type;
        this.deviceName = deviceName;
    }

    public AudioDeviceType.Type getType() {
        return type;
    }

    public void setType(AudioDeviceType.Type type) {
        this.type = type;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

}
