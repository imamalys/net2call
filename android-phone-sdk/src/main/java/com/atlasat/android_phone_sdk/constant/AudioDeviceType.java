package com.atlasat.android_phone_sdk.constant;

public interface AudioDeviceType {
    public enum Type {
        /**
         * Unknown. <br/>
         * <br/>
         */
        Unknown(0),

        /**
         * Microphone. <br/>
         * <br/>
         */
        Microphone(1),

        /**
         * Earpiece. <br/>
         * <br/>
         */
        Earpiece(2),

        /**
         * Speaker. <br/>
         * <br/>
         */
        Speaker(3),

        /**
         * Bluetooth. <br/>
         * <br/>
         */
        Bluetooth(4),

        /**
         * Bluetooth A2DP. <br/>
         * <br/>
         */
        BluetoothA2DP(5),

        /**
         * Telephony. <br/>
         * <br/>
         */
        Telephony(6),

        /**
         * AuxLine. <br/>
         * <br/>
         */
        AuxLine(7),

        /**
         * GenericUsb. <br/>
         * <br/>
         */
        GenericUsb(8),

        /**
         * Headset. <br/>
         * <br/>
         */
        Headset(9),

        /**
         * Headphones. <br/>
         * <br/>
         */
        Headphones(10),

        /**
         * Hearing Aid. <br/>
         * <br/>
         */
        HearingAid(11);

        protected final int mValue;

        private Type (int value) {
            mValue = value;
        }

        static public Type fromInt(int value) throws RuntimeException {
            switch(value) {
                case 0: return Unknown;
                case 1: return Microphone;
                case 2: return Earpiece;
                case 3: return Speaker;
                case 4: return Bluetooth;
                case 5: return BluetoothA2DP;
                case 6: return Telephony;
                case 7: return AuxLine;
                case 8: return GenericUsb;
                case 9: return Headset;
                case 10: return Headphones;
                case 11: return HearingAid;
                default:
                    throw new RuntimeException("Unhandled enum value " + value + " for Type");
            }
        }

        public int toInt() {
            return mValue;
        }
    };
}
