package com.atlasat.net2call.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.atlasat.net2call.R;
import com.atlasat.net2call.databinding.DialogConfirmationBinding;
import com.atlasat.net2call.databinding.DialogLoadingBinding;

public class ConfirmationDialog extends DialogFragment {
    private View.OnClickListener yesBtn;
    public ConfirmationDialog(View.OnClickListener yesBtn) {
        this.yesBtn = yesBtn;
    }
    @Override
    public int getTheme() {
        return R.style.DialogStyle;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        DialogConfirmationBinding binding = DialogConfirmationBinding.inflate(inflater);
        setCancelable(false);

        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        binding.btnYes.setOnClickListener(yesBtn);
        binding.btnNo.setOnClickListener(v-> dismiss());
        return binding.getRoot();
    }
}