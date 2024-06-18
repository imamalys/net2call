package com.atlasat.net2call.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.atlasat.net2call.databinding.FragmentDialBinding;

public class DialFragment extends Fragment {

    public DialFragment() {
        // Required empty public constructor
    }

    private FragmentDialBinding binding;
    private DialListener dialListener;

    public static DialFragment newInstance() {
        return new DialFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDialBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.dialLayout.ivCall.setOnClickListener(v-> {
            if (binding.dialLayout.tvNumber.getText().toString().length() != 0) {
//                dialListener.outgoingCall(String.format("sip:%s@sip2sip.info", binding.dialLayout.tvNumber.getText().toString()));
                dialListener.inviteCall(String.format("sip:%s@cloud.optimaccs.com", binding.dialLayout.tvNumber.getText().toString()));
            } else {
                Toast.makeText(requireActivity(), "Please input number", Toast.LENGTH_SHORT).show();
            }
        });

        binding.dialLayout.tvNumber.setText("7777");
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
        binding.dialLayout.ivCorrection.setOnClickListener(v-> {
            addInputNumber("correction");
        });
    }

    private void addInputNumber(String input) {
        if (input.equals("correction")) {
            if (binding.dialLayout.tvNumber.getText().toString().length() != 0) {
                binding.dialLayout.tvNumber.setText(binding.dialLayout.tvNumber.getText().toString().substring(0, binding.dialLayout.tvNumber.getText().toString().length() - 1));
            }
        } else {
            if (binding.dialLayout.tvNumber.getText().toString().length() < 15) {
                binding.dialLayout.tvNumber.setText(String.format("%s%s", binding.dialLayout.tvNumber.getText().toString(), input));
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dialListener = (DialListener) context;
    }

    public interface DialListener {
        void inviteCall(String sipURI);
    }
}