package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemRoomKindBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewholders.RoomKindViewHolder;

public class RoomKindAdapter extends ExtendedAdapter<RoomKindObservable, RoomKindViewHolder> {

    public RoomKindAdapter() {
        super();
    }

    @NonNull
    @Override
    public RoomKindViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemRoomKindBinding binding = RecyclerViewItemRoomKindBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomKindViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomKindViewHolder holder, int position) {
        if (baseObservables != null) {
            RecyclerViewItemRoomKindBinding binding = RecyclerViewItemRoomKindBinding.bind(holder.itemView);
            RoomKindObservable roomKindObservable = baseObservables.get(position);
        }
    }

}
