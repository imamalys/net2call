package com.atlasat.net2call.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.atlasat.net2call.R;
import com.atlasat.net2call.databinding.FragmentDTMFBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DTMFFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DTMFFragment extends Fragment {
    private FragmentDTMFBinding binding;
    private DTMFListener dtmfListener;

    public DTMFFragment() {
        // Required empty public constructor
    }

    public static DTMFFragment newInstance() {
        return new DTMFFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDTMFBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.dialLayout.tv1.setOnClickListener(v -> {
            addInputNumber("1");
        });
        binding.dialLayout.tv2.setOnClickListener(v -> {
            addInputNumber("2");
        });
        binding.dialLayout.tv3.setOnClickListener(v -> {
            addInputNumber("3");
        });
        binding.dialLayout.tv4.setOnClickListener(v -> {
            addInputNumber("4");
        });
        binding.dialLayout.tv5.setOnClickListener(v -> {
            addInputNumber("5");
        });
        binding.dialLayout.tv6.setOnClickListener(v -> {
            addInputNumber("6");
        });
        binding.dialLayout.tv7.setOnClickListener(v -> {
            addInputNumber("7");
        });
        binding.dialLayout.tv8.setOnClickListener(v -> {
            addInputNumber("8");
        });
        binding.dialLayout.tv9.setOnClickListener(v -> {
            addInputNumber("9");
        });
        binding.dialLayout.tvAsterisk.setOnClickListener(v -> {
            addInputNumber("*");
        });
        binding.dialLayout.tvHashtag.setOnClickListener(v -> {
            addInputNumber("#");
        });
        binding.dialLayout.tv0.setOnClickListener(v -> {
            addInputNumber("0");
        });

        binding.dialLayout.ivCall.setOnClickListener(v-> {
            dtmfListener.onHide();
        });
        binding.dialLayout.ivCorrection.setVisibility(View.GONE);
        binding.dialLayout.ivCall.setImageDrawable(getResources().getDrawable(R.drawable.numpad, null));
        binding.dialLayout.ivCall.setColorFilter(getResources().getColor(R.color.white, null));
        binding.dialLayout.tvNumber.setTextColor(getResources().getColor(R.color.white, null));
    }

    private void addInputNumber(String input) {
        if (binding.dialLayout.tvNumber.getText().toString().length() > 0) {
            binding.dialLayout.tvNumber.setText(String.format("%s%s", binding.dialLayout.tvNumber.getText().toString(), input));
        } else {
            binding.dialLayout.tvNumber.setText(input);
        }

        dtmfListener.onSendDTMF(binding.dialLayout.tvNumber.getText().toString());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dtmfListener = (DTMFListener) context;
    }

    public interface DTMFListener {
        void onSendDTMF(String number);
        void onHide();
    }
}