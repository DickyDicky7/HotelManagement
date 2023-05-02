package com.example.hotelmanagement.viewholders;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.databinding.ListItemSampleBookedroomBinding;
import com.example.hotelmanagement.databinding.ListItemSampleRoomBinding;

public class RoomViewHolder extends RecyclerView.ViewHolder {

    public RoomViewHolder(ListItemSampleRoomBinding binding) {
        super(binding.getRoot());
    }

    public RoomViewHolder(ListItemSampleBookedroomBinding binding) {
        super(binding.getRoot());
    }

}
