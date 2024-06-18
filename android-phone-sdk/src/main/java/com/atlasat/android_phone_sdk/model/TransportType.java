package com.atlasat.android_phone_sdk.model;

public enum TransportType {
    /**
     * <br/>
     */
    Udp(0),

    /**
     * <br/>
     */
    Tcp(1),

    /**
     * <br/>
     */
    Tls(2),

    /**
     * <br/>
     */
    Dtls(3);

    protected final int mValue;

    private TransportType (int value) {
        mValue = value;
    }

    static public TransportType fromInt(int value) throws RuntimeException {
        switch(value) {
            case 0: return Udp;
            case 1: return Tcp;
            case 2: return Tls;
            case 3: return Dtls;
            default:
                throw new RuntimeException("Unhandled enum value " + value + " for TransportType");
        }
    }

    static protected org.linphone.core.TransportType[] fromIntArray(int[] values) throws RuntimeException {
        int arraySize = values.length;
        org.linphone.core.TransportType[] enumArray = new org.linphone.core.TransportType[arraySize];
        for (int i = 0; i < arraySize; i++) {
            enumArray[i] = org.linphone.core.TransportType.fromInt(values[i]);
        }
        return enumArray;
    }

    static protected int[] toIntArray(org.linphone.core.TransportType[] values) throws RuntimeException {
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
