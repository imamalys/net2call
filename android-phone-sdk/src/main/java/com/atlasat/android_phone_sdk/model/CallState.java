package com.atlasat.android_phone_sdk.model;

public enum CallState {
    /**
     * Initial state. <br/>
     * <br/>
     */
    Idle(0),

    /**
     * Incoming call received. <br/>
     * <br/>
     */
    IncomingReceived(1),

    /**
     * PushIncoming call received. <br/>
     * <br/>
     */
    PushIncomingReceived(2),

    /**
     * Outgoing call initialized. <br/>
     * <br/>
     */
    OutgoingInit(3),

    /**
     * Outgoing call in progress. <br/>
     * <br/>
     */
    OutgoingProgress(4),

    /**
     * Outgoing call ringing. <br/>
     * <br/>
     */
    OutgoingRinging(5),

    /**
     * Outgoing call early media. <br/>
     * <br/>
     */
    OutgoingEarlyMedia(6),

    /**
     * Connected. <br/>
     * <br/>
     */
    Connected(7),

    /**
     * Streams running. <br/>
     * <br/>
     */
    StreamsRunning(8),

    /**
     * Pausing. <br/>
     * <br/>
     */
    Pausing(9),

    /**
     * Paused. <br/>
     * <br/>
     */
    Paused(10),

    /**
     * Resuming. <br/>
     * <br/>
     */
    Resuming(11),

    /**
     * Referred. <br/>
     * <br/>
     */
    Referred(12),

    /**
     * Error. <br/>
     * <br/>
     */
    Error(13),

    /**
     * Call end. <br/>
     * <br/>
     */
    End(14),

    /**
     * Paused by remote. <br/>
     * <br/>
     */
    PausedByRemote(15),

    /**
     * The call's parameters are updated for example when video is asked by remote. <br/>
     * <br/>
     */
    UpdatedByRemote(16),

    /**
     * We are proposing early media to an incoming call. <br/>
     * <br/>
     */
    IncomingEarlyMedia(17),

    /**
     * We have initiated a call update. <br/>
     * <br/>
     */
    Updating(18),

    /**
     * The call object is now released. <br/>
     * <br/>
     */
    Released(19),

    /**
     * The call is updated by remote while not yet answered (SIP UPDATE in early<br/>
     * dialog received) <br/>
     * <br/>
     */
    EarlyUpdatedByRemote(20),

    /**
     * We are updating the call while not yet answered (SIP UPDATE in early dialog<br/>
     * sent) <br/>
     * <br/>
     */
    EarlyUpdating(21);

    protected final int mValue;

    private CallState(int value) {
        mValue = value;
    }

    static public CallState fromInt(int value) throws RuntimeException {
        switch(value) {
            case 0: return Idle;
            case 1: return IncomingReceived;
            case 2: return PushIncomingReceived;
            case 3: return OutgoingInit;
            case 4: return OutgoingProgress;
            case 5: return OutgoingRinging;
            case 6: return OutgoingEarlyMedia;
            case 7: return Connected;
            case 8: return StreamsRunning;
            case 9: return Pausing;
            case 10: return Paused;
            case 11: return Resuming;
            case 12: return Referred;
            case 13: return Error;
            case 14: return End;
            case 15: return PausedByRemote;
            case 16: return UpdatedByRemote;
            case 17: return IncomingEarlyMedia;
            case 18: return Updating;
            case 19: return Released;
            case 20: return EarlyUpdatedByRemote;
            case 21: return EarlyUpdating;
            default:
                throw new RuntimeException("Unhandled enum value " + value + " for State");
        }
    }

    public int toInt() {
        return mValue;
    }
};
