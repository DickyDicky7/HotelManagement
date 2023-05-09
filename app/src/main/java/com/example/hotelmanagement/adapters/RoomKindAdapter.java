package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.databinding.ListItemSampleRoomkindBinding;
import com.example.hotelmanagement.observable.RoomKindObservable;
import com.example.hotelmanagement.viewholders.RoomKindViewHolder;

import java.util.List;

public class RoomKindAdapter extends RecyclerView.Adapter<RoomKindViewHolder> {

    public List<RoomKindObservable> roomKindObservables;

    @NonNull
    @Override
    public RoomKindViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSampleRoomkindBinding binding = ListItemSampleRoomkindBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomKindViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomKindViewHolder holder, int position) {
        ListItemSampleRoomkindBinding binding = ListItemSampleRoomkindBinding.bind(holder.itemView);
        RoomKindObservable roomKindObservable = roomKindObservables.get(position);
    }

    @Override
    public int getItemCount() {
        return roomKindObservables.size();
    }

}
