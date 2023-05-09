package com.example.hotelmanagement.adapters_viewmodels;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.ListItemSampleBookedroomBinding;
import com.example.hotelmanagement.databinding.ListItemSampleRoomBinding;
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
            ListItemSampleRoomBinding binding = ListItemSampleRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RoomViewHolder(binding);
        }
        ListItemSampleBookedroomBinding binding = ListItemSampleBookedroomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        List<RoomObservable> roomObservables = modelState.getValue();
        if (roomObservables != null) {
            RoomObservable roomObservable = roomObservables.get(position);
            if (holder.getItemViewType() == 1) {
                ListItemSampleRoomBinding binding = ListItemSampleRoomBinding.bind(holder.itemView);
            } else {
                ListItemSampleBookedroomBinding binding = ListItemSampleBookedroomBinding.bind(holder.itemView);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        List<RoomObservable> roomObservables = modelState.getValue();
        return roomObservables != null ? (roomObservables.get(position).getIsOccupied() ? 1 : 0) : 0;
    }

}
