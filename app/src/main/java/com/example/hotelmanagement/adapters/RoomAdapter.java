package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.databinding.RecyclerViewItemRoomBinding;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomOccupiedBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewholders.RoomViewHolder;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;

import java.util.List;
import java.util.Optional;

public class RoomAdapter extends ExtendedAdapter<RoomObservable, RoomViewHolder> {

    private final RoomListener roomListener;

    public RoomAdapter(FragmentActivity fragmentActivity,RoomListener roomListener) {
        super(fragmentActivity);
        this.roomListener = roomListener;
    }

    public RoomAdapter(FragmentActivity fragmentActivity, RoomListener roomListener,List<RoomObservable> roomObservables) {
        super(fragmentActivity, roomObservables);
        this.roomListener=roomListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            RecyclerViewItemRoomBinding binding = RecyclerViewItemRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RoomViewHolder(binding);
        }
        RecyclerViewItemRoomOccupiedBinding binding = RecyclerViewItemRoomOccupiedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        if (baseObservables != null) {

            RoomObservable roomObservable = baseObservables.get(position);

            RoomKindViewModel roomKindViewModel = new ViewModelProvider(fragmentActivity).get(RoomKindViewModel.class);
            List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState().getValue();
            Optional<RoomKindObservable> optionalRoomKindObservable = Optional.empty();
            if (roomKindObservables != null) {
                optionalRoomKindObservable = roomKindObservables.stream().filter(roomKindObservable -> roomKindObservable.getId().equals(roomObservable.getRoomKindId())).findFirst();
            }

            if (holder.getItemViewType() == 0) {
                RecyclerViewItemRoomBinding binding = RecyclerViewItemRoomBinding.bind(holder.itemView);
                binding.itemRRoomName.setText(roomObservable.getName());
                if (optionalRoomKindObservable.isPresent()) {
                    RoomKindObservable roomKindObservable = optionalRoomKindObservable.get();
                    binding.itemRRoomKind.setText(roomKindObservable.getName());
                } else {
                    binding.itemRRoomKind.setText("");
                }
                binding.itemRoom.setOnClickListener(view -> roomListener.onRoomClick(roomObservable));
            } else {
                RecyclerViewItemRoomOccupiedBinding binding = RecyclerViewItemRoomOccupiedBinding.bind(holder.itemView);
                binding.itemBrRoomName.setText(roomObservable.getName());
                if (optionalRoomKindObservable.isPresent()) {
                    RoomKindObservable roomKindObservable = optionalRoomKindObservable.get();
                    binding.itemBrRoomKind.setText(roomKindObservable.getName());
                } else {
                    binding.itemBrRoomKind.setText("");
                }
                binding.itemBookedroom.setOnClickListener(view -> roomListener.onRoomClick(roomObservable));
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        return baseObservables != null ? (baseObservables.get(position).getIsOccupied() ? 1 : 0) : 0;
    }

    public interface RoomListener{
        void onRoomClick(RoomObservable roomObservable);
    }

}
