package com.example.hotelmanagement.adapters_viewmodels;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemRoomKindBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewholders.RoomKindViewHolder;

import java.util.List;

public class RoomKindAdapterViewModel extends ExtendedAdapterViewModel<RoomKindObservable, RoomKindViewHolder> {

    public RoomKindAdapterViewModel() {
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
        List<RoomKindObservable> roomKindObservables = modelState.getValue();
        if (roomKindObservables != null) {
            RecyclerViewItemRoomKindBinding binding = RecyclerViewItemRoomKindBinding.bind(holder.itemView);
            RoomKindObservable roomKindObservable = roomKindObservables.get(position);
        }
    }

}
