package com.example.hotelmanagement.adapter.implementation;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.hotelmanagement.adapter.abstraction.ExtendedAdapter;
import com.example.hotelmanagement.adapter.viewholder.implementation.RoomKindViewHolder;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomKindBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;

import java.util.List;

public class RoomKindAdapter extends ExtendedAdapter<RoomKindObservable, RoomKindViewHolder> {

    private final RoomKindListener roomKindListener;

    public RoomKindAdapter(FragmentActivity fragmentActivity, RoomKindListener roomKindListener) {
        super(fragmentActivity);
        this.roomKindListener = roomKindListener;
    }

    public RoomKindAdapter(FragmentActivity fragmentActivity, RoomKindListener roomKindListener, List<RoomKindObservable> roomKindObservables) {
        super(fragmentActivity, roomKindObservables);
        this.roomKindListener = roomKindListener;
    }

    @NonNull
    @Override
    public RoomKindViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemRoomKindBinding binding = RecyclerViewItemRoomKindBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomKindViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RoomKindViewHolder holder, int position) {

        RecyclerViewItemRoomKindBinding binding = RecyclerViewItemRoomKindBinding.bind(holder.itemView);
        RoomKindObservable roomKindObservable = items.get(position);

        binding.itemRoomKindNameTextView.setText(roomKindObservable.getName());
        binding.itemRoomKindAreaTextView.setText(roomKindObservable.getArea() + " m\u00B2");
        binding.itemRoomKindCapacityTextView.setText(String.valueOf(roomKindObservable.getCapacity()));
        binding.itemRoomKindNumberOfBedsTextView.setText(String.valueOf(roomKindObservable.getNumberOfBeds()));
        Glide.with(fragmentActivity).load(roomKindObservable.getImageURL()).centerCrop().transform(new
                RoundedCorners(30)).into(binding.itemRoomKindMainImageView);

        binding.itemRoomKindEditButton.setOnClickListener(view -> roomKindListener.onEditRoomKindClick(roomKindObservable));
        binding.itemRoomKindDeleButton.setOnClickListener(view -> roomKindListener.onDeleRoomKindClick(roomKindObservable));

    }

    public interface RoomKindListener {

        void onJustRoomKindClick(RoomKindObservable roomKindObservable);

        void onEditRoomKindClick(RoomKindObservable roomKindObservable);

        void onDeleRoomKindClick(RoomKindObservable roomKindObservable);

    }

}
