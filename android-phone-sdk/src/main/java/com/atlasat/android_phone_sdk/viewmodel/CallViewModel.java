package com.atlasat.android_phone_sdk.viewmodel;

import static org.linphone.core.AudioDevice.Type.Bluetooth;
import static org.linphone.core.AudioDevice.Type.BluetoothA2DP;
import static org.linphone.core.AudioDevice.Type.Headphones;
import static org.linphone.core.AudioDevice.Type.Headset;

import android.Manifest;
import android.os.Build;
import android.os.SystemClock;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.viewmodel.ViewModelInitializer;

import com.atlasat.android_phone_sdk.SDKCore;
import com.atlasat.android_phone_sdk.base.BaseViewModel;
import com.atlasat.android_phone_sdk.constant.AudioDeviceType;
import com.atlasat.android_phone_sdk.helper.PermissionHelper;
import com.atlasat.android_phone_sdk.model.CallState;
import com.atlasat.android_phone_sdk.model.CustomHeader;
import com.atlasat.android_phone_sdk.model.RegistrationSIPState;
import com.atlasat.android_phone_sdk.model.SIPConfigResponse;
import com.atlasat.android_phone_sdk.utils.AudioRouteUtils;

import org.linphone.core.Account;
import org.linphone.core.AccountParams;
import org.linphone.core.Address;
import org.linphone.core.AudioDevice;
import org.linphone.core.AuthInfo;
import org.linphone.core.Call;
import org.linphone.core.CallParams;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.MediaEncryption;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;
import org.linphone.core.tools.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CallViewModel extends BaseViewModel {
    public MutableLiveData<SIPConfigResponse> sipRegisterResponse = new MutableLiveData<>();
    public MutableLiveData<com.atlasat.android_phone_sdk.model.Call> currentCall = new MutableLiveData<>();
    public MutableLiveData<List<com.atlasat.android_phone_sdk.model.AudioDevice>> audioDeviceList = new MutableLiveData<>();
    public CoreListenerStub coreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(@NonNull Core core, @NonNull Account account, RegistrationState state, @NonNull String message) {
            super.onAccountRegistrationStateChanged(core, account, state, message);
            switch (state) {
                case Ok:
                    isLoading.postValue(false);
                    sipRegisterResponse.postValue(new SIPConfigResponse(RegistrationSIPState.Ok, message));
                    break;
                case Failed:
                    isLoading.postValue(false);
                    sipRegisterResponse.postValue(new SIPConfigResponse( RegistrationSIPState.Failed, message));
                    break;
                case Progress:
                case Cleared:
                case Refreshing:
                    break;
            }
        }

        @Override
        public void onAudioDevicesListUpdated(@NonNull Core core) {
            super.onAudioDevicesListUpdated(core);
            ArrayList<com.atlasat.android_phone_sdk.model.AudioDevice> audioDevices = new ArrayList<>();
            for (AudioDevice audioDevice : core.getAudioDevices()) {
                if (audioDevice.getType() == Bluetooth
                        || audioDevice.getType() == BluetoothA2DP
                        || audioDevice.getType() == Headphones
                        || audioDevice.getType() == Headset) {
                    com.atlasat.android_phone_sdk.model.AudioDevice device =
                            new com.atlasat.android_phone_sdk.model.AudioDevice(
                                    AudioDeviceType.Type.fromInt(audioDevice.getType().toInt()),
                                    audioDevice.getDeviceName());
                    com.atlasat.android_phone_sdk.model.AudioDevice checkDevice = audioDevices.stream()
                            .filter(p -> p.getDeviceName().equals(device.getDeviceName()))
                            .findAny()
                            .orElse(null);
                    if (checkDevice == null) {
                        audioDevices.add(device);
                    }
                }
            }
            audioDevices.add(new com.atlasat.android_phone_sdk.model.AudioDevice(AudioDeviceType.Type.Speaker, "Speaker"));
            audioDevices.add(new com.atlasat.android_phone_sdk.model.AudioDevice(AudioDeviceType.Type.Earpiece, "Earpiece"));
            audioDeviceList.postValue(audioDevices);
        }

        @Override
        public void onCallStateChanged(@NonNull Core core, @NonNull Call call, Call.State state, @NonNull String message) {
            com.atlasat.android_phone_sdk.model.Call initCall = new com.atlasat.android_phone_sdk.model.Call();
            initCall.setCallName(call.getRemoteAddress().asStringUriOnly());
            switch (state) {
                case Idle:
                    break;
                case IncomingReceived:
                    initCall.setCallState(CallState.IncomingReceived);
                    currentCall.postValue(initCall);
                    break;
                case PushIncomingReceived:
                    break;
                case OutgoingInit:
                    break;
                case OutgoingProgress:
                    initCall.setCallState(CallState.OutgoingProgress);
                    currentCall.postValue(initCall);
                    break;
                case OutgoingRinging:
                    break;
                case OutgoingEarlyMedia:
                    break;
                case StreamsRunning:
                case Connected:
                    initCall.setCallState(CallState.Connected);
                    currentCall.postValue(initCall);
                    break;
                case Pausing:
                case PausedByRemote:
                case Paused:
                    initCall.setCallState(CallState.Paused);
                    currentCall.postValue(initCall);
                    break;
                case Resuming:
                    break;
                case Referred:
                    break;
                case Error:
                    break;
                case End:
                case Released:
                    initCall.setCallState(CallState.Released);
                    currentCall.postValue(initCall);
                    break;
                case UpdatedByRemote:
                    break;
                case IncomingEarlyMedia:
                    break;
                case Updating:
                    break;
                case EarlyUpdatedByRemote:
                    break;
                case EarlyUpdating:
                    break;
            }
        }
    };

    public void setDuration(Chronometer timer) {
        timer.setBase(SystemClock.elapsedRealtime() - (1000L * SDKCore.singleHolder.get().core.getCurrentCall().getDuration()));
        timer.start();
    }
    public void acceptCall() {
        Objects.requireNonNull(SDKCore.singleHolder.get().core.getCurrentCall()).accept();
    }

    public void decline() {
        Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        call.terminate();
    }

    public void hangUp() {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return;
        Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        call.terminate();
    }

    public void pauseOrResume() {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return;

        Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        if (call == null) return;

        if (call.getState() != Call.State.Paused && call.getState() != Call.State.Pausing) {
            // If our call isn't paused, let's pause it
            call.pause();
        } else if (call.getState() != Call.State.Resuming) {
            // Otherwise let's resume it
            call.resume();
        }
    }

    public void getCurrentCall() {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return;

        Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        if (call == null) return;

        com.atlasat.android_phone_sdk.model.Call initCall = new com.atlasat.android_phone_sdk.model.Call();
        initCall.setCallName(call.getRemoteAddress().asStringUriOnly());
        switch (call.getState()) {
            case Idle:
                break;
            case IncomingReceived:
                initCall.setCallState(CallState.IncomingReceived);
                currentCall.postValue(initCall);
                break;
            case PushIncomingReceived:
                break;
            case OutgoingInit:
                break;
            case OutgoingProgress:
                initCall.setCallState(CallState.OutgoingProgress);
                currentCall.postValue(initCall);
                break;
            case OutgoingRinging:
                break;
            case OutgoingEarlyMedia:
                break;
            case StreamsRunning:
            case Connected:
                initCall.setCallState(CallState.Connected);
                currentCall.postValue(initCall);
                break;
            case Pausing:
                break;
            case PausedByRemote:
            case Paused:
                initCall.setCallState(CallState.Paused);
                currentCall.postValue(initCall);
                break;
            case Resuming:
                break;
            case Referred:
                break;
            case Error:
                break;
            case End:
            case Released:
                initCall.setCallState(CallState.Released);
                currentCall.postValue(initCall);
                break;
            case UpdatedByRemote:
                break;
            case IncomingEarlyMedia:
                break;
            case Updating:
                break;
            case EarlyUpdatedByRemote:
                break;
            case EarlyUpdating:
                break;
        }
    }

    public void setMicEnabled(boolean isEnabled) {
        SDKCore.singleHolder.get().core.setMicEnabled(isEnabled);
    }

    public void setOutputAudioDevice(int deviceType) {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return;
        Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        if (call == null) return;

        switch (AudioDevice.Type.fromInt(deviceType)) {
            case Speaker:
                AudioRouteUtils.routeAudioToSpeaker(call, true);
                break;
            case Earpiece:
                AudioRouteUtils.routeAudioToEarpiece(call, true);
                break;
            case Headset:
                AudioRouteUtils.routeAudioToHeadset(call, true);
                break;
            case Bluetooth:
                AudioRouteUtils.routeAudioToBluetooth(call, true);
                break;
        }
    }

    public void setInputAudioDevice(int deviceType) {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return;
        Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        if (call == null) return;

        switch (AudioDevice.Type.fromInt(deviceType)) {
            case Microphone:
                AudioRouteUtils.routeAudioToSpeaker(call, false);
                break;
            case Bluetooth:
                AudioRouteUtils.routeAudioToEarpiece(call, false);
                break;
        }
    }

    public void inviteCall(String sipURI) {
        Address remoteAddress = Factory.instance().createAddress(sipURI);
        assert remoteAddress != null;
        CallParams params = SDKCore.singleHolder.get().core.createCallParams(null);
        assert params != null;
        params.setMediaEncryption(MediaEncryption.None);
        params.setLowBandwidthEnabled(true);
        SDKCore.singleHolder.get().core.inviteAddressWithParams(remoteAddress, params);
    }

    public String attendedTransfer() {
        String message = "[Call Controls] Can't do an attended transfer without a current call";
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return message;
        Call currentCall = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        if (currentCall == null) return message;

        if (SDKCore.singleHolder.get().core.getCallsNb() <= 1) {
            message = "[Call Controls] Need at least two calls to do an attended transfer";
            Log.e(message);
            return message;
        }

        Call callToTransferTo = null;
        for (Call call : SDKCore.singleHolder.get().core.getCalls()) {
            if (call.getState() == Call.State.Paused) {
                callToTransferTo = call;
                break;
            }
        }
        if (callToTransferTo == null) {
            message = "[Call Controls] Couldn't find a call in Paused state to transfer current call to";
            Log.e(message);
            return message;
        }

        message = "[Call Controls] Doing an attended transfer between active call [" +
                currentCall.getRemoteAddress().asStringUriOnly() +
                "] and paused call [" +
                callToTransferTo.getRemoteAddress().asStringUriOnly() +
                "]";
        Log.i(message);
        int result = callToTransferTo.transferToAnother(currentCall);
        if (result != 0) {
            message = "[Call Controls] Attended transfer failed!";
            Log.e(message);
            return message;
        }

        return message;
    }

    public String blindTransfer(String addressToCall) {
        String message = "Couldn't find a call to transfer";
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return message;
        Call currentCall = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        if (currentCall == null) return message;
        Address address = SDKCore.singleHolder.get().core.interpretUrl(addressToCall, false);
        if (address != null) {
            message = "Transferring current call to ";
            Log.i("[Context] " + message + addressToCall);
            currentCall.transferTo(address);
            return message + addressToCall;
        } else {
            Log.e("[Context] " + message);
            return message;
        }
    }

    public void inviteCall(String sipURI, List<CustomHeader> customHeaders) {
        Address remoteAddress = Factory.instance().createAddress(sipURI);
        assert remoteAddress != null;
        CallParams params = SDKCore.singleHolder.get().core.createCallParams(null);
        assert params != null;
        params.setMediaEncryption(MediaEncryption.None);
        params.setLowBandwidthEnabled(true);
        for (CustomHeader customHeader: customHeaders) {
            params.addCustomHeader(customHeader.getName(), customHeader.getValue());
        }
        SDKCore.singleHolder.get().core.inviteAddressWithParams(remoteAddress, params);
    }

    public String[] checkCallPermission() {
        ArrayList<String> permissionsRequiredList = new ArrayList<>();

        if (!PermissionHelper.singletonHolder.get().hasRecordAudioPermission()) {
            Log.i("[Call Activity] Asking for RECORD_AUDIO permission");
            permissionsRequiredList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!PermissionHelper.singletonHolder.get().hasPostNotificationsPermission()) {
                // Don't check the following the previous permission is being asked
                Log.i("[Dialer] Asking for POST_NOTIFICATIONS permission");
                permissionsRequiredList.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (!permissionsRequiredList.isEmpty()) {
            String[] permissionsRequired = new String[permissionsRequiredList.size()];
            permissionsRequiredList.toArray(permissionsRequired);
            return permissionsRequired;
        }

        return new String[0];
    }

    public CallViewModel() {

    }

    public void sendDTMF(String number) {
        if (SDKCore.singleHolder.get().core.getCallsNb() == 0) return;
        Call call = SDKCore.singleHolder.get().core.getCurrentCall() != null
                ? SDKCore.singleHolder.get().core.getCurrentCall()
                : SDKCore.singleHolder.get().core.getCalls()[0];
        if (call == null) return;

        call.sendDtmfs(number);
    }

    public <T> void setClass(Class<T> clazz) {
        SDKCore.singleHolder.get().clazz = clazz;
    }

    public void setCurrentListener() {
        SDKCore.singleHolder.get().core.addListener(coreListener);
    }

    public void removeListener() {
        SDKCore.singleHolder.get().core.removeListener(coreListener);
    }

    public void connect(String username, String password, String domain, com.atlasat.android_phone_sdk.model.TransportType transportType, String port) {
        isLoading.postValue(true);
        if (SDKCore.singleHolder.get().core.getDefaultAccount() != null) {
            removeAccount();
        }
        AuthInfo authInfo = SDKCore.singleHolder.get().factory.createAuthInfo(username, null, password, null, null, domain);
        AccountParams accountParams = SDKCore.singleHolder.get().core.createAccountParams();
        Address identity = SDKCore.singleHolder.get().factory.createAddress(String.format("sip:%s@%s", username, domain));
        accountParams.setIdentityAddress(identity);
        Address address = SDKCore.singleHolder.get().factory.createAddress(
                String.format("sip:%s%s", domain,
                        port != null
                                ? String.format(":%s", port)
                                : ""));
        switch (transportType) {
            case Udp:
                address.setTransport(TransportType.Udp);
                break;
            case Tcp:
                address.setTransport(TransportType.Tcp);
                break;
            case Tls:
                address.setTransport(TransportType.Tls);
                break;
            case Dtls:
                address.setTransport(TransportType.Dtls);
                break;
        }
        if (port != null) {
            address.setPort(Integer.parseInt(port));
        }
        accountParams.setServerAddress(address);
        accountParams.setRegisterEnabled(true);
        Account account = SDKCore.singleHolder.get().core.createAccount(accountParams);
        SDKCore.singleHolder.get().core.addAuthInfo(authInfo);
        SDKCore.singleHolder.get().core.addAccount(account);
        SDKCore.singleHolder.get().core.setDefaultAccount(account);
        SDKCore.singleHolder.get().core.addListener(coreListener);
        SDKCore.singleHolder.get().core.start();
    }

    public void removeAccount() {
        // To completely remove an Account
        Account account = SDKCore.singleHolder.get().core.getDefaultAccount();
        if (account == null) {
            return;
        }
        SDKCore.singleHolder.get().core.removeAccount(account);

        // To remove all accounts use
        SDKCore.singleHolder.get().core.clearAccounts();

        // Same for auth info
        SDKCore.singleHolder.get().core.clearAllAuthInfo();
    }

    public static final ViewModelInitializer<CallViewModel> initializer = new ViewModelInitializer<>(
            CallViewModel.class,
            creationExtras -> new CallViewModel()
    );
}
