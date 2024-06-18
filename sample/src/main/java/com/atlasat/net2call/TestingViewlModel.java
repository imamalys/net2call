package com.atlasat.net2call;

import androidx.viewbinding.ViewBinding;

import com.atlasat.android_phone_sdk.base.BaseActivity;
import com.atlasat.android_phone_sdk.viewmodel.CallViewModel;

public class TestingViewlModel extends BaseActivity<ViewBinding, CallViewModel> {

    @Override
    protected void setupView() {
        //Pausing current Call
        viewModel.pauseOrResume();

        viewModel.inviteCall("sip:201002@<realm>");

        //wait until Connected
        viewModel.currentCall.observe(this, currentCall -> {
            if (currentCall.getCallState() != null) {
                switch (currentCall.getCallState()) {
                    //do attended transfer
                    case Connected:
                        viewModel.attendedTransfer();
                        break;
                }
            }
        });
    }

    @Override
    protected void setupViewModel() {

    }

    @Override
    protected CallViewModel createViewModel() {
        return null;
    }

    @Override
    protected ViewBinding createViewBinding() {
        return null;
    }

    @Override
    protected void onLoadingShow() {

    }

    @Override
    protected void onLoadingDismiss() {

    }
}
