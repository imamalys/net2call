package com.atlasat.net2call.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Chronometer;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.atlasat.android_phone_sdk.base.BaseActivity;
import com.atlasat.android_phone_sdk.constant.AppConstant;
import com.atlasat.android_phone_sdk.constant.AudioDeviceType;
import com.atlasat.android_phone_sdk.model.Call;
import com.atlasat.android_phone_sdk.model.CallState;
import com.atlasat.android_phone_sdk.model.CustomHeader;
import com.atlasat.android_phone_sdk.viewmodel.CallViewModel;
import com.atlasat.net2call.R;
import com.atlasat.net2call.databinding.ActivityOptionBinding;
import com.atlasat.net2call.dialog.LoadingDialog;
import com.atlasat.net2call.fragment.CallFragment;
import com.atlasat.net2call.fragment.DTMFFragment;
import com.atlasat.net2call.fragment.DialFragment;
import com.atlasat.net2call.fragment.IncomingCallFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("UseCompatLoadingForDrawables")
public class OptionActivity extends BaseActivity<ActivityOptionBinding, CallViewModel>
        implements IncomingCallFragment.IncomingCallListener,
            CallFragment.CallListener,
            DialFragment.DialListener,
            DTMFFragment.DTMFListener {
    private LoadingDialog loadingDialog;
    private Call currentCall;
    private CallFragment callFragment;
    @Override
    protected void setupView() {
        loadingDialog = new LoadingDialog();

        if (getIntent() != null) {
            if (getIntent().hasExtra(AppConstant.CALL_STATE)) {
                viewModel.getCurrentCall();
            } else if (getIntent().hasExtra("MENU")) {
                if (!Objects.equals(getIntent().getStringExtra("MENU"), "dial")) {
                    viewModel.getCurrentCall();
                } else {
                    setHomeMenu(Objects.requireNonNull(getIntent().getStringExtra("MENU")));
                }
            }
        }

        viewBinding.ivDial.setOnClickListener(v-> {
            setHomeMenu("dial");
        });

        viewBinding.ivSettings.setOnClickListener(v-> {
            setHomeMenu("settings");
        });
    }

    @Override
    protected void setupViewModel() {
        viewModel.setCurrentListener();
        viewModel.currentCall.observe(this, currentCall -> {
            this.currentCall = currentCall;
            if (currentCall.getCallState() != null) {
                switch (currentCall.getCallState()) {
                    case IncomingReceived:
                        setHomeMenu("incoming_call");
                        break;
                    case OutgoingProgress:
                        setHomeMenu("outgoing_progress");
                        break;
                    case Connected:
                        setHomeMenu("connected");
                        break;
                    case Paused:
                        setCallStatus(true);
                        break;
                    case Resuming:
                        setCallStatus(false);
                        break;
                    case Released:
                        setHomeMenu("released");
                    default:
                        break;
                }
            }
        });

        viewModel.audioDeviceList.observe(this, audioDevices -> {
            if (audioDevices.size() > 2 && callFragment != null) {
                callFragment.setAudioUpdate(audioDevices);
            }
        });
    }

    @Override
    protected CallViewModel createViewModel() {
        return new CallViewModel();
    }

    @Override
    protected ActivityOptionBinding createViewBinding() {
        viewBinding = ActivityOptionBinding.inflate(getLayoutInflater());
        return viewBinding;
    }

    @Override
    protected void onLoadingShow() {
        loadingDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onLoadingDismiss() {
        if (loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void setCallStatus(boolean isHold) {
        callFragment.setCallStatus(isHold);
    }

    private void setHomeMenu(String menu) {
        Fragment fragment = new Fragment();
        switch (menu) {
            case "dial":
                callFragment = null;
                currentCall = null;
                fragment = DialFragment.newInstance();
                viewBinding.ivDial.setImageDrawable(getResources().getDrawable(R.drawable.dial_active, null));
                viewBinding.ivSettings.setImageDrawable(getResources().getDrawable(R.drawable.setting_inactive, null));
                break;
            case "dtmf":
                callFragment = null;
                fragment = DTMFFragment.newInstance();
                break;
            case "incoming_call":
                fragment = IncomingCallFragment.newInstance(currentCall.getCallName());
                break;
            case "outgoing_progress":
                setCallFragment(CallState.OutgoingProgress.toInt());
                break;
            case "connected":
                setCallFragment(CallState.Connected.toInt());
                break;
            case "released":
                finish();
                break;
        }
        if (!menu.equals("outgoing_progress") && !menu.equals("connected")) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(viewBinding.flHome.getId(),fragment, null)
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    public void setCallFragment(int callState) {
        if (callFragment != null) {
            callFragment.setUpdate(callState);
        } else {
            callFragment = CallFragment.newInstance(callState, currentCall.getCallName());

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(viewBinding.flHome.getId(), callFragment, null)
                    .setReorderingAllowed(true)
                    .commit();
        }
    }

    @Override
    public void inviteCall(String sipURI) {
        viewModel.inviteCall(sipURI);
    }

    @Override
    public void onAccept() {
        viewModel.acceptCall();

    }

    @Override
    public void onDecline() {
        viewModel.decline();
    }

    @Override
    public void onSetChronometer(Chronometer timer) {
        viewModel.setDuration(timer);
    }

    @Override
    public void setMicEnabled(boolean isEnabled) {
        viewModel.setMicEnabled(isEnabled);
    }

    @Override
    public void endCall() {
        viewModel.hangUp();
    }

    @Override
    public void setAudioDevices(int device) {
        viewModel.setOutputAudioDevice(device);
        if (device == AudioDeviceType.Type.Bluetooth.toInt()) {
            viewModel.setInputAudioDevice(device);
        } else {
            viewModel.setInputAudioDevice(AudioDeviceType.Type.Microphone.toInt());
        }
    }

    @Override
    public void setCallHold() {
        viewModel.pauseOrResume();
    }

    @Override
    public void onDTMFMode() {
        setHomeMenu("dtmf");
    }

    @Override
    public void onSendDTMF(String number) {
        viewModel.sendDTMF(number);
    }

    @Override
    public void onHide() {
        setHomeMenu("connected");
    }
}