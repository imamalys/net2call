package com.atlasat.net2call.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.atlasat.android_phone_sdk.constant.AppConstant;
import com.atlasat.net2call.databinding.FragmentIncomingCallBinding;

public class IncomingCallFragment extends Fragment {

    public IncomingCallFragment() {
        // Required empty public constructor
    }

    private IncomingCallListener incomingCallListener;
    private String callerName;

    private FragmentIncomingCallBinding binding;

    public static IncomingCallFragment newInstance(String callerName) {
        IncomingCallFragment incomingCallFragment = new IncomingCallFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.CALLER_NAME, callerName);
        incomingCallFragment.setArguments(bundle);
        return incomingCallFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            callerName = getArguments().getString(AppConstant.CALLER_NAME);
        }
        binding = FragmentIncomingCallBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tvCallerId.setText(callerName);
        binding.ivCall.setOnClickListener(v-> {
            incomingCallListener.onAccept();
        });

        binding.ivEndCall.setOnClickListener(v-> {
            incomingCallListener.onDecline();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        incomingCallListener = (IncomingCallListener) context;
    }

    public interface IncomingCallListener {
        void onAccept();
        void onDecline();
    }
}