package com.example.hotelmanagement.adaptersviewmodels;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.ListItemSampleRoomkindBinding;
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
        ListItemSampleRoomkindBinding binding = ListItemSampleRoomkindBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomKindViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomKindViewHolder holder, int position) {
        List<RoomKindObservable> roomKindObservables = modelState.getValue();
        if (roomKindObservables != null) {
            ListItemSampleRoomkindBinding binding = ListItemSampleRoomkindBinding.bind(holder.itemView);
            RoomKindObservable roomKindObservable = roomKindObservables.get(position);
        }
    }

}
