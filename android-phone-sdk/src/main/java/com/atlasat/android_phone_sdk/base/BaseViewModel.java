package com.atlasat.android_phone_sdk.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BaseViewModel extends ViewModel {
    protected MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    protected MutableLiveData<Boolean> showLoading() {
        return isLoading;
    }
}
