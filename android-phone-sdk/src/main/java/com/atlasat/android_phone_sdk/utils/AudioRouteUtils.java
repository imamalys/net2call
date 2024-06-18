package com.atlasat.android_phone_sdk.utils;

import com.atlasat.android_phone_sdk.SDKCore;

import org.linphone.core.AudioDevice;
import org.linphone.core.Call;
import org.linphone.core.Conference;
import org.linphone.core.tools.Log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class AudioRouteUtils {

    public static void routeAudioToEarpiece(Call call, boolean output) {
        routeAudioTo(call, Collections.singletonList(AudioDevice.Type.Earpiece), output);
    }

    public static void routeAudioToEarpiece(boolean output) {
        routeAudioToEarpiece(null, output);
    }

    public static void routeAudioToSpeaker(Call call, boolean output) {
        routeAudioTo(call, Collections.singletonList(AudioDevice.Type.Speaker), output);
    }

    public static void routeAudioToSpeaker(boolean output) {
        routeAudioToSpeaker(null, output);
    }

    public static void routeAudioToBluetooth(Call call, boolean output) {
        routeAudioTo(call, Arrays.asList(AudioDevice.Type.Bluetooth, AudioDevice.Type.HearingAid), output);
    }

    public static void routeAudioToBluetooth(boolean output) {
        routeAudioToBluetooth(null, output);
    }

    public static void routeAudioToHeadset(Call call, boolean output) {
        routeAudioTo(call, Arrays.asList(AudioDevice.Type.Headphones, AudioDevice.Type.Headset), output);
    }


    public static void routeAudioToHeadset(boolean output) {
        routeAudioToHeadset(null, output);
    }

    public static boolean isSpeakerAudioRouteCurrentlyUsed(Call call) {
        Call currentCall = (SDKCore.singleHolder.get().core.getCallsNb() > 0) ?
                (call != null ? call : (SDKCore.singleHolder.get().core.getCurrentCall() != null
                        ? SDKCore.singleHolder.get().core.getCurrentCall()
                        : SDKCore.singleHolder.get().core.getCalls()[0]))
                : null;

        if (currentCall == null) {
            Log.w("[Audio Route Helper] No call found, checking audio route on Core");
        }

        AudioDevice audioDevice = ((currentCall != null)
                ? currentCall.getOutputAudioDevice()
                : SDKCore.singleHolder.get().core.getOutputAudioDevice());

        if (audioDevice == null) {
            return false;
        }

        Log.i("[Audio Route Helper] Playback audio device currently in use is [" + audioDevice.getDeviceName() + " (" + audioDevice.getDriverName() + ") " + audioDevice.getType() + "]");
        return audioDevice.getType() == AudioDevice.Type.Speaker;
    }

    public static boolean isBluetoothAudioRouteCurrentlyUsed(Call call) {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) {
            Log.w("[Audio Route Helper] No call found, so bluetooth audio route isn't used");
            return false;
        }

        Call currentCall = (call != null) ? call : (SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0]);

        AudioDevice audioDevice = currentCall.getOutputAudioDevice();

        if (audioDevice == null) {
            return false;
        }

        Log.i("[Audio Route Helper] Playback audio device currently in use is [" + audioDevice.getDeviceName() + " (" + audioDevice.getDriverName() + ") " + audioDevice.getType() + "]");
        return audioDevice.getType() == AudioDevice.Type.Bluetooth || audioDevice.getType() == AudioDevice.Type.HearingAid;
    }

    public static boolean isBluetoothAudioRouteAvailable() {
        for (AudioDevice audioDevice : SDKCore.singleHolder.get().core.getAudioDevices()) {
            if ((audioDevice.getType() == AudioDevice.Type.Bluetooth || audioDevice.getType() == AudioDevice.Type.HearingAid)
                    && audioDevice.hasCapability(AudioDevice.Capabilities.CapabilityPlay)) {
                Log.i("[Audio Route Helper] Found bluetooth audio device [" + audioDevice.getDeviceName() + " (" + audioDevice.getDriverName() + ")]");
                return true;
            }
        }
        return false;
    }

    public static boolean isHeadsetAudioRouteAvailable() {
        for (AudioDevice audioDevice : SDKCore.singleHolder.get().core.getAudioDevices()) {
            if ((audioDevice.getType() == AudioDevice.Type.Headset || audioDevice.getType() == AudioDevice.Type.Headphones)
                    && audioDevice.hasCapability(AudioDevice.Capabilities.CapabilityPlay)) {
                Log.i("[Audio Route Helper] Found headset/headphones audio device [" + audioDevice.getDeviceName() + " (" + audioDevice.getDriverName() + ")]");
                return true;
            }
        }
        return false;
    }

    public static String getAudioPlaybackDeviceIdForCallRecordingOrVoiceMessage() {
        String headphonesCard = null;
        String bluetoothCard = null;
        String speakerCard = null;
        String earpieceCard = null;

        for (AudioDevice device : SDKCore.singleHolder.get().core.getAudioDevices()) {
            if (device.hasCapability(AudioDevice.Capabilities.CapabilityPlay)) {
                switch (device.getType()) {
                    case Headphones:
                    case Headset:
                        headphonesCard = device.getId();
                        break;
                    case Bluetooth:
                    case HearingAid:
                        bluetoothCard = device.getId();
                        break;
                    case Speaker:
                        speakerCard = device.getId();
                        break;
                    case Earpiece:
                        earpieceCard = device.getId();
                        break;
                    default:
                        break;
                }
            }
        }

        Log.i("[Audio Route Helper] Found headset/headphones/hearingAid sound card [" + headphonesCard + "], bluetooth sound card [" + bluetoothCard + "], speaker sound card [" + speakerCard + "] and earpiece sound card [" + earpieceCard + "]");
        return (headphonesCard != null) ? headphonesCard : (bluetoothCard != null) ? bluetoothCard : (speakerCard != null) ? speakerCard : earpieceCard;
    }

    public static AudioDevice getAudioRecordingDeviceForVoiceMessage() {
        AudioDevice bluetoothAudioDevice = null;
        AudioDevice headsetAudioDevice = null;
        AudioDevice builtinMicrophone = null;

        for (AudioDevice device : SDKCore.singleHolder.get().core.getAudioDevices()) {
            if (device.hasCapability(AudioDevice.Capabilities.CapabilityRecord)) {
                switch (device.getType()) {
                    case Bluetooth:
                    case HearingAid:
                        bluetoothAudioDevice = device;
                        break;
                    case Headset:
                    case Headphones:
                        headsetAudioDevice = device;
                        break;
                    case Microphone:
                        builtinMicrophone = device;
                        break;
                    default:
                        break;
                }
            }
        }

        Log.i("[Audio Route Helper] Found headset/headphones/hearingAid [" + (headsetAudioDevice != null ? headsetAudioDevice.getId() : null) + "], bluetooth [" + (bluetoothAudioDevice != null ? bluetoothAudioDevice.getId() : null) + "] and builtin microphone [" + (builtinMicrophone != null ? builtinMicrophone.getId() : null) + "]");
        return (headsetAudioDevice != null) ? headsetAudioDevice : (bluetoothAudioDevice != null) ? bluetoothAudioDevice : builtinMicrophone;
    }

    private static void routeAudioTo(Call call, List<AudioDevice.Type> types, boolean output) {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return;
        Call currentCall = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];

        applyAudioRouteChange(call != null ? call : currentCall, types, output);
    }

    public static void applyAudioRouteChange(Call call, List<AudioDevice.Type> types, boolean output) {
        Call currentCall = (SDKCore.singleHolder.get().core.getCallsNb() > 0) ?
                (call != null ? call : (SDKCore.singleHolder.get().core.getCurrentCall() != null
                        ? SDKCore.singleHolder.get().core.getCurrentCall()
                        : SDKCore.singleHolder.get().core.getCalls()[0]))
                : null;

        if (currentCall == null) {
            Log.w("[Audio Route Helper] No call found, setting audio route on Core");
        }

        Conference conference = SDKCore.singleHolder.get().core.getConference();
        AudioDevice.Capabilities capability = output
                ? AudioDevice.Capabilities.CapabilityPlay
                : AudioDevice.Capabilities.CapabilityRecord;
        String preferredDriver = output
                ? SDKCore.singleHolder.get().core.getDefaultOutputAudioDevice() != null
                ? SDKCore.singleHolder.get().core.getDefaultOutputAudioDevice().getDriverName()
                : null
                : SDKCore.singleHolder.get().core.getDefaultInputAudioDevice() != null
                ? SDKCore.singleHolder.get().core.getDefaultInputAudioDevice().getDriverName()
                : null;

        List<AudioDevice> extendedAudioDevices = Arrays.asList(SDKCore.singleHolder.get().core.getExtendedAudioDevices());
        Log.i("[Audio Route Helper] Looking for an " + (output ? "output" : "input") +
                " audio device with capability [" + capability + "], driver name [" + preferredDriver +
                "] and type [" + types + "] in extended audio devices list (size " + extendedAudioDevices.size() + ")");

        AudioDevice foundAudioDevice = null;
        for (AudioDevice device : extendedAudioDevices) {
            if (preferredDriver != null && preferredDriver.equals(device.getDriverName()) &&
                    types.contains(device.getType()) && device.hasCapability(capability)) {
                foundAudioDevice = device;
                break;
            }
        }

        AudioDevice audioDevice;
        if (foundAudioDevice == null) {
            Log.w("[Audio Route Helper] Failed to find an audio device with capability [" + capability +
                    "], driver name [" + preferredDriver + "] and type [" + types + "]");
            for (AudioDevice device : extendedAudioDevices) {
                if (types.contains(device.getType()) && device.hasCapability(capability)) {
                    foundAudioDevice = device;
                    break;
                }
            }
        }

        audioDevice = foundAudioDevice;

        if (audioDevice == null) {
            Log.e("[Audio Route Helper] Couldn't find audio device with capability [" + capability +
                    "] and type [" + types + "]");
            for (AudioDevice device : extendedAudioDevices) {
                Log.i("[Audio Route Helper] Extended audio device: [" + device.getDeviceName() +
                        " (" + device.getDriverName() + ") " + device.getType() + " / " + device.getCapabilities() + "]");
            }
            return;
        }

        if (conference != null && conference.isIn()) {
            Log.i("[Audio Route Helper] Found [" + audioDevice.getType() + "] " +
                    (output ? "playback" : "recorder") + " audio device [" + audioDevice.getDeviceName() +
                    " (" + audioDevice.getDriverName() + ")], routing conference audio to it");
            if (output) {
                conference.setOutputAudioDevice(audioDevice);
            } else {
                conference.setInputAudioDevice(audioDevice);
            }
        } else if (currentCall != null) {
            Log.i("[Audio Route Helper] Found [" + audioDevice.getType() + "] " +
                    (output ? "playback" : "recorder") + " audio device [" + audioDevice.getDeviceName() +
                    " (" + audioDevice.getDriverName() + ")], routing call audio to it");
            if (output) {
                currentCall.setOutputAudioDevice(audioDevice);
            } else {
                currentCall.setInputAudioDevice(audioDevice);
            }
        } else {
            Log.i("[Audio Route Helper] Found [" + audioDevice.getType() + "] " +
                    (output ? "playback" : "recorder") + " audio device [" + audioDevice.getDeviceName() +
                    " (" + audioDevice.getDriverName() + ")], changing core default audio device");
            if (output) {
                SDKCore.singleHolder.get().core.setOutputAudioDevice(audioDevice);
            } else {
                SDKCore.singleHolder.get().core.setInputAudioDevice(audioDevice);
            }
        }
    }
}

