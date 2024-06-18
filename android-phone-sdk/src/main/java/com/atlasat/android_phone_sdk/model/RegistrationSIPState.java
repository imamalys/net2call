package com.atlasat.android_phone_sdk.model;

public enum RegistrationSIPState {
    /**
     * Initial state for registrations. <br/>
     * <br/>
     */
    None(0),

    /**
     * Registration is in progress. <br/>
     * <br/>
     */
    Progress(1),

    /**
     * Registration is successful. <br/>
     * <br/>
     */
    Ok(2),

    /**
     * Unregistration succeeded. <br/>
     * <br/>
     */
    Cleared(3),

    /**
     * Registration failed. <br/>
     * <br/>
     */
    Failed(4),

    /**
     * Registration refreshing. <br/>
     * <br/>
     */
    Refreshing(5);

    protected final int mValue;

    private RegistrationSIPState (int value) {
        mValue = value;
    }
    static public RegistrationSIPState fromInt(int value) throws RuntimeException {
        switch(value) {
            case 0: return None;
            case 1: return Progress;
            case 2: return Ok;
            case 3: return Cleared;
            case 4: return Failed;
            case 5: return Refreshing;
            default:
                throw new RuntimeException("Unhandled enum value " + value + " for RegistrationState");
        }
    }

    static protected RegistrationSIPState[] fromIntArray(int[] values) throws RuntimeException {
        int arraySize = values.length;
        RegistrationSIPState[] enumArray = new RegistrationSIPState[arraySize];
        for (int i = 0; i < arraySize; i++) {
            enumArray[i] = RegistrationSIPState.fromInt(values[i]);
        }
        return enumArray;
    }

    static protected int[] toIntArray(RegistrationSIPState[] values) throws RuntimeException {
        int arraySize = values.length;
        int[] intArray = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            intArray[i] = values[i].toInt();
        }
        return intArray;
    }

    public int toInt() {
        return mValue;
    }
}
