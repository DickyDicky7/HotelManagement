package com.example.hotelmanagement.adapters;

import android.annotation.SuppressLint;
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
        if (baseObservables != null) {

            RecyclerViewItemRoomKindBinding binding = RecyclerViewItemRoomKindBinding.bind(holder.itemView);
            RoomKindObservable roomKindObservable = baseObservables.get(position);

            binding.roomkindName.setText(roomKindObservable.getName());
            binding.roomArea.setText(roomKindObservable.getArea() + " m\u00B2");
            binding.roomBed.setText(String.valueOf(roomKindObservable.getNumberOfBeds()));
            binding.roomMaxGuest.setText(String.valueOf(roomKindObservable.getCapacity()));
            Glide.with(fragmentActivity).load(roomKindObservable.getImageURL()).centerCrop().transform(new RoundedCorners(30)).into(binding.itemRkImage);

            binding.itemRkBtnEdit.setOnClickListener(view -> roomKindListener.onRoomKindClick(roomKindObservable));

        }
    }

    public interface RoomKindListener {
        void onRoomKindClick(RoomKindObservable roomKindObservable);
    }

}
