package com.atlasat.net2call.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.atlasat.net2call.R;
import com.atlasat.net2call.databinding.DialogLoadingBinding;

public class LoadingDialog extends DialogFragment {
    public LoadingDialog() {

    }
    @Override
    public int getTheme() {
        return R.style.DialogStyle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogLoadingBinding binding = DialogLoadingBinding.inflate(inflater);
        setCancelable(false);
        return binding.getRoot();
    }
}
