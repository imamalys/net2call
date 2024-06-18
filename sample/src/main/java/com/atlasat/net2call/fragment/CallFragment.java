package com.atlasat.net2call.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.atlasat.android_phone_sdk.constant.AppConstant;
import com.atlasat.android_phone_sdk.constant.AudioDeviceType;
import com.atlasat.android_phone_sdk.model.AudioDevice;
import com.atlasat.android_phone_sdk.model.CallState;
import com.atlasat.android_phone_sdk.utils.AudioRouteUtils;
import com.atlasat.net2call.R;
import com.atlasat.net2call.databinding.FragmentCallBinding;

import java.util.List;

@SuppressLint("UseCompatLoadingForDrawables")
public class CallFragment extends Fragment {

    public CallFragment() {
        // Required empty public constructor
    }

    private CallListener callListener;

    private FragmentCallBinding binding;
    private boolean mic = true;
    private CallState callState;
    private boolean speakerActive = false;
    private String callerName;
    private boolean isOutgoing;
    private boolean isBluetooth = false;
    private boolean isHeadset = false;

    public static CallFragment newInstance(int callState, String callerName) {
        Bundle bundle = new Bundle();
        bundle.putInt(AppConstant.CALL_STATE, callState);
        bundle.putString(AppConstant.CALLER_NAME, callerName);

        CallFragment fragment = new CallFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setAudioUpdate(List<AudioDevice> audioDevices) {
        updateAudioOutput(audioDevices);
    }

    public void setUpdate(int callState) {
        this.callState = CallState.fromInt(callState);
        setCallState(this.callState);
    }

    public void setCallStatus(boolean isHold) {
        if (isHold) {
            binding.tvCallHold.setVisibility(View.VISIBLE);
            binding.ivPause.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.resume_icon, null));
        } else {
            binding.tvCallHold.setVisibility(View.GONE);
            binding.ivPause.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.pause_icon, null));
        }
        binding.ivPause.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            callState = CallState.fromInt(getArguments().getInt(AppConstant.CALL_STATE));
            callerName = getArguments().getString(AppConstant.CALLER_NAME);
        }
        binding = FragmentCallBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.tvCallerId.setText(callerName);
        binding.ivEndCall.setOnClickListener(v-> {
            callListener.endCall();
        });

        setCallState(callState);

        binding.ivNumpad.setOnClickListener(v -> callListener.onDTMFMode());
        binding.ivMic.setOnClickListener(v-> {
            if (mic) {
                callListener.setMicEnabled(false);
                binding.ivMic.setImageDrawable(getResources().getDrawable(R.drawable.microphone_icon, null));
            } else {
                callListener.setMicEnabled(true);
                binding.ivMic.setImageDrawable(getResources().getDrawable(R.drawable.mute_icon, null));
            }
            mic = !mic;
        });

        binding.ivSpeaker.setOnClickListener(v -> {
            if (speakerActive) {
                if (isBluetooth && AudioRouteUtils.isBluetoothAudioRouteAvailable()) {
                    callListener.setAudioDevices(AudioDeviceType.Type.Bluetooth.toInt());
                } else if (isHeadset && AudioRouteUtils.isHeadsetAudioRouteAvailable()) {
                    callListener.setAudioDevices(AudioDeviceType.Type.Headset.toInt());
                } else {
                    callListener.setAudioDevices(AudioDeviceType.Type.Earpiece.toInt());
                }
                binding.ivSpeaker.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.speaker_icon, requireActivity().getTheme()));
            } else {
                callListener.setAudioDevices(AudioDeviceType.Type.Speaker.toInt());
                binding.ivSpeaker.setImageDrawable(requireActivity().getResources().getDrawable(R.drawable.speaker_off, requireActivity().getTheme()));
            }
            speakerActive = !speakerActive;
        });

        binding.ivPause.setOnClickListener(v-> {
            callListener.setCallHold();
        });
    }

    private void updateAudioOutput(List<AudioDevice> audioDevices) {
        isBluetooth = audioDevices.stream().filter(p-> p.getType() == AudioDeviceType.Type.Bluetooth)
                .findFirst()
                .orElse(null) != null;
        isHeadset = audioDevices.stream().filter(p-> p.getType() == AudioDeviceType.Type.Headset)
                .findFirst()
                .orElse(null) != null;
        if (isBluetooth && AudioRouteUtils.isBluetoothAudioRouteAvailable()) {
            callListener.setAudioDevices(AudioDeviceType.Type.Bluetooth.toInt());
        } else if (isHeadset && AudioRouteUtils.isHeadsetAudioRouteAvailable()) {
            callListener.setAudioDevices(AudioDeviceType.Type.Headset.toInt());
        }
    }
    private void setCallState(CallState callState) {
        switch (callState) {
            case Connected:
                isOutgoing = false;
                Chronometer timer  = binding.tvTime;
                setCallStatus(false);
                callListener.onSetChronometer(timer);
                break;
            case OutgoingProgress:
                isOutgoing = true;
                break;
        }

        if (isOutgoing) {
            binding.tvCallInfo.setText("Outgoing Call");
            binding.tvCallInfo.setVisibility(View.VISIBLE);
        } else {
            binding.tvCallInfo.setVisibility(View.GONE);
            binding.tvCallInfo.setText("");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callListener = (CallListener) context;
    }

    public interface CallListener {
        void onSetChronometer(Chronometer timer);
        void setMicEnabled(boolean isEnabled);
        void endCall();
        void setAudioDevices(int device);
        void setCallHold();
        void onDTMFMode();
    }
}