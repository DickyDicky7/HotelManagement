package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemRoomBinding;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomOccupiedBinding;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewholders.RoomViewHolder;

public class RoomAdapter extends ExtendedAdapter<RoomObservable, RoomViewHolder> {

    public RoomAdapter() {
        super();
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            RecyclerViewItemRoomBinding binding = RecyclerViewItemRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RoomViewHolder(binding);
        }
        RecyclerViewItemRoomOccupiedBinding binding = RecyclerViewItemRoomOccupiedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        if (baseObservables != null) {
            RoomObservable roomObservable = baseObservables.get(position);
            if (holder.getItemViewType() == 1) {
                RecyclerViewItemRoomBinding binding = RecyclerViewItemRoomBinding.bind(holder.itemView);
            } else {
                RecyclerViewItemRoomOccupiedBinding binding = RecyclerViewItemRoomOccupiedBinding.bind(holder.itemView);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return baseObservables != null ? (baseObservables.get(position).getIsOccupied() ? 1 : 0) : 0;
    }

}
