package com.atlasat.net2call.activity;

import android.content.Intent;

import androidx.lifecycle.ViewModelProvider;

import com.atlasat.android_phone_sdk.base.BaseActivity;
import com.atlasat.android_phone_sdk.model.TransportType;
import com.atlasat.android_phone_sdk.viewmodel.CallViewModel;
import com.atlasat.net2call.BuildConfig;
import com.atlasat.net2call.databinding.ActivityHomeBinding;
import com.atlasat.net2call.dialog.ConfirmationDialog;
import com.atlasat.net2call.dialog.LoadingDialog;

public class HomeActivity extends BaseActivity<ActivityHomeBinding, CallViewModel> {
    private LoadingDialog loadingDialog;
    private boolean isAccountReady = false;
    private ConfirmationDialog dialog;
    private boolean allowLoading = false;
    @Override
    protected void setupView() {
        loadingDialog = new LoadingDialog();
        connect();

        viewBinding.ivCall.setOnClickListener(v-> {
            if (isAccountReady) {
               callService();
            } else {
                loadingDialog.show(getSupportFragmentManager(), "");
                allowLoading = true;
                connect();
            }
        });

        viewBinding.ivDial.setOnClickListener(v-> {
            startOptionActivity("dial");
        });

        viewModel.currentCall.observe(this, currentCall -> {
            if (currentCall.getCallState() != null) {
                switch (currentCall.getCallState()) {
                    case IncomingReceived:
                        startOptionActivity("incoming_call");
                        break;
                    case OutgoingProgress:
                        startOptionActivity("outgoing_progress");
                        break;
                    case Connected:
                        startOptionActivity("connected");
                        break;
                }
            }
        });
    }

    private void callService() {
        dialog = new ConfirmationDialog(view -> {
            viewModel.inviteCall("sip:7777@cloud.optimaccs.com");
            dialog.dismiss();
        });
        dialog.show(getSupportFragmentManager(), "");
    }

    private void startOptionActivity(String menu) {
        Intent intent = new Intent(this, OptionActivity.class);
        intent.putExtra("MENU", menu);
        startActivity(intent);
    }

    private void connect() {
        viewModel.connect(BuildConfig.USERNAME, BuildConfig.PASSWORD, "cloud.optimaccs.com", TransportType.Udp, "5160");
    }

    @Override
    protected void setupViewModel() {
        if (viewModel.checkCallPermission().length != 0) {
            requestPermissions(viewModel.checkCallPermission(), 0);
        }

        viewModel.setClass(HomeActivity.class);

        viewModel.sipRegisterResponse.observe(this, sipConfigResponse -> {
            switch (sipConfigResponse.getState()) {
                case Ok:
                    isAccountReady = true;
                    if (allowLoading) {
                        callService();
                    }
                    break;
                case Failed:
                    isAccountReady = false;
                default:
                    break;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loadingDialog.onDestroy();
    }

    @Override
    protected CallViewModel createViewModel() {
        return viewModel = new ViewModelProvider(this,
                ViewModelProvider.Factory.from(CallViewModel.initializer)
        ).get(CallViewModel.class);
    }

    @Override
    protected ActivityHomeBinding createViewBinding() {
        return ActivityHomeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void onLoadingShow() {
        if (allowLoading) {
            loadingDialog.show(getSupportFragmentManager(), "");
        }
    }

    @Override
    protected void onLoadingDismiss() {
        if (allowLoading) {
            if (loadingDialog.getDialog() != null && loadingDialog.getDialog().isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }
}