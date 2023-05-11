package com.example.hotelmanagement.adapters_viewmodels;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemRoomBinding;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomOccupiedBinding;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewholders.RoomViewHolder;

import java.util.List;

public class RoomAdapterViewModel extends ExtendedAdapterViewModel<RoomObservable, RoomViewHolder> {

    public RoomAdapterViewModel() {
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
        List<RoomObservable> roomObservables = modelState.getValue();
        if (roomObservables != null) {
            RoomObservable roomObservable = roomObservables.get(position);
            if (holder.getItemViewType() == 1) {
                RecyclerViewItemRoomBinding binding = RecyclerViewItemRoomBinding.bind(holder.itemView);
            } else {
                RecyclerViewItemRoomOccupiedBinding binding = RecyclerViewItemRoomOccupiedBinding.bind(holder.itemView);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        List<RoomObservable> roomObservables = modelState.getValue();
        return roomObservables != null ? (roomObservables.get(position).getIsOccupied() ? 1 : 0) : 0;
    }

}
