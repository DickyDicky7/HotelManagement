package com.example.hotelmanagement.viewholders;

import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.databinding.RecyclerViewItemRoomBinding;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomOccupiedBinding;

public class RoomViewHolder extends RecyclerView.ViewHolder {

    public RoomViewHolder(RecyclerViewItemRoomBinding binding) {
        super(binding.getRoot());
    }

    public RoomViewHolder(RecyclerViewItemRoomOccupiedBinding binding) {
        super(binding.getRoot());
    }

}
