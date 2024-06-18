package com.atlasat.android_phone_sdk.model;

import java.util.ArrayList;

public class Notifiable {
    private final int notificationId;
    private final ArrayList<NotifiableMessage> messages;

    private boolean isGroup;
    private String groupTitle;
    private String localIdentity;
    private String myself;
    private String remoteAddress;

    public Notifiable(int notificationId) {
        this.notificationId = notificationId;
        this.messages = new ArrayList<>();
    }

    public int getNotificationId() {
        return notificationId;
    }

    public ArrayList<NotifiableMessage> getMessages() {
        return messages;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public String getLocalIdentity() {
        return localIdentity;
    }

    public void setLocalIdentity(String localIdentity) {
        this.localIdentity = localIdentity;
    }

    public String getMyself() {
        return myself;
    }

    public void setMyself(String myself) {
        this.myself = myself;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
