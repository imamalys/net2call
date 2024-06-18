package com.atlasat.android_phone_sdk.model;

import android.graphics.Bitmap;
import android.net.Uri;

import org.linphone.core.Friend;

public class NotifiableMessage {
    private String message;
    private Friend friend;
    private String sender;
    private long time;
    private Bitmap senderAvatar;
    private Uri filePath;
    private String fileMime;
    private boolean isOutgoing;
    private boolean isReaction;
    private String reactionToMessageId;
    private String reactionFrom;

    // Constructor
    public NotifiableMessage(
            String message,
            Friend friend,
            String sender,
            long time,
            Bitmap senderAvatar,
            Uri filePath,
            String fileMime,
            boolean isOutgoing,
            boolean isReaction,
            String reactionToMessageId,
            String reactionFrom
    ) {
        this.message = message;
        this.friend = friend;
        this.sender = sender;
        this.time = time;
        this.senderAvatar = senderAvatar;
        this.filePath = filePath;
        this.fileMime = fileMime;
        this.isOutgoing = isOutgoing;
        this.isReaction = isReaction;
        this.reactionToMessageId = reactionToMessageId;
        this.reactionFrom = reactionFrom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Bitmap getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(Bitmap senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public Uri getFilePath() {
        return filePath;
    }

    public void setFilePath(Uri filePath) {
        this.filePath = filePath;
    }

    public String getFileMime() {
        return fileMime;
    }

    public void setFileMime(String fileMime) {
        this.fileMime = fileMime;
    }

    public boolean isOutgoing() {
        return isOutgoing;
    }

    public void setOutgoing(boolean outgoing) {
        isOutgoing = outgoing;
    }

    public boolean isReaction() {
        return isReaction;
    }

    public void setReaction(boolean reaction) {
        isReaction = reaction;
    }

    public String getReactionToMessageId() {
        return reactionToMessageId;
    }

    public void setReactionToMessageId(String reactionToMessageId) {
        this.reactionToMessageId = reactionToMessageId;
    }

    public String getReactionFrom() {
        return reactionFrom;
    }

    public void setReactionFrom(String reactionFrom) {
        this.reactionFrom = reactionFrom;
    }
}
