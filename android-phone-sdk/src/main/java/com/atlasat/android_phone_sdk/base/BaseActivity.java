package com.atlasat.android_phone_sdk.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity<VB extends ViewBinding, VM extends BaseViewModel> extends AppCompatActivity {
    public VM viewModel;
    public VB viewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewBinding = createViewBinding();
        viewModel = createViewModel();
        setContentView(viewBinding.getRoot());
        initView();
    }

    private void initView() {
        setupViewModel();
        setupView();

        viewModel.showLoading().observe(this, loading -> {
            if (loading) {
                onLoadingShow();
            } else {
                onLoadingDismiss();
            }
        });
    }

    protected abstract void setupView();

    protected abstract void setupViewModel();

    protected abstract VM createViewModel();

    protected abstract VB createViewBinding();

    protected abstract void onLoadingShow();
    protected abstract void onLoadingDismiss();
}
