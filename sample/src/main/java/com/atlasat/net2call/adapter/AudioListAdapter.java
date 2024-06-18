package com.atlasat.net2call.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atlasat.android_phone_sdk.model.AudioDevice;
import com.atlasat.net2call.R;
import com.atlasat.net2call.databinding.AdapterAudioListBinding;

import java.util.ArrayList;
import java.util.List;

@SuppressLint({"DefaultLocale", "SetTextI18n"})
public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ItemViewHolder> {
    private List<AudioDevice> itemList;
    private ItemClickListener itemClickListener;
    private Context context;

    public AudioListAdapter(Context context, List<AudioDevice>  itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(AudioDevice data);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(AdapterAudioListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.setData(itemList.get(position));
        holder.binding.getRoot().setOnClickListener(v-> itemClickListener.onItemClick(itemList.get(position)));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setData(ArrayList<AudioDevice> data) {
        itemList = data;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        AdapterAudioListBinding binding;

        public ItemViewHolder(AdapterAudioListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setData(AudioDevice data) {
            if (data != null) {
                binding.tvName.setText(data.getDeviceName());
                if (data.isActive()) {
                    binding.ivCheck.setVisibility(View.VISIBLE);
                    binding.llBackground.setBackgroundColor(context.getResources().getColor(R.color.blue_color, null));
                    binding.tvName.setTextColor(context.getResources().getColor(R.color.blue_color, null));
                } else {
                    binding.ivCheck.setVisibility(View.GONE);
                    binding.llBackground.setBackgroundColor(context.getResources().getColor(R.color.white, null));
                    binding.tvName.setTextColor(context.getResources().getColor(R.color.black, null));
                }
            }
        }
    }
}
