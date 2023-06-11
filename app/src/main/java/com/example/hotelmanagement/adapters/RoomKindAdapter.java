package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomKindBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewholders.RoomKindViewHolder;

import java.util.List;

public class RoomKindAdapter extends ExtendedAdapter<RoomKindObservable, RoomKindViewHolder> {

    public RoomKindAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public RoomKindAdapter(FragmentActivity fragmentActivity, List<RoomKindObservable> roomKindObservables) {
        super(fragmentActivity, roomKindObservables);
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

            binding.roomkindName.setText(roomKindObservable.getName());
            binding.roomArea.setText(String.valueOf(roomKindObservable.getArea()));
            binding.roomBed.setText(String.valueOf(roomKindObservable.getNumberOfBeds()));
            binding.roomMaxGuest.setText(String.valueOf(roomKindObservable.getCapacity()));
            Glide.with(fragmentActivity).load(roomKindObservable.getImageURL()).centerCrop().transform(new RoundedCorners(30)).into(binding.itemRkImage);

        }
    }

}
