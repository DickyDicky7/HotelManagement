package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.databinding.ListItemSampleBookedroomBinding;
import com.example.hotelmanagement.databinding.ListItemSampleRoomBinding;
import com.example.hotelmanagement.observable.RoomObservable;
import com.example.hotelmanagement.viewholders.RoomViewHolder;

import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomViewHolder> {

    public List<RoomObservable> roomObservables;

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            ListItemSampleRoomBinding binding = ListItemSampleRoomBinding.inflate(LayoutInflater.from(parent.getContext()));
            return new RoomViewHolder(binding);
        }
        ListItemSampleBookedroomBinding binding = ListItemSampleBookedroomBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        RoomObservable roomObservable = roomObservables.get(position);
        if (holder.getItemViewType() == 1) {
            ListItemSampleRoomBinding binding = ListItemSampleRoomBinding.bind(holder.itemView);
        } else {
            ListItemSampleBookedroomBinding binding = ListItemSampleBookedroomBinding.bind(holder.itemView);
        }
    }

    @Override
    public int getItemCount() {
        return roomObservables.size();
    }

    @Override
    public int getItemViewType(int position) {
        return roomObservables.get(position).getIsOccupied() ? 1 : 0;
    }
}