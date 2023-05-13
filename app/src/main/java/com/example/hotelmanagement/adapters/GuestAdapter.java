package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemGuestBinding;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewholders.GuestViewHolder;

public class GuestAdapter extends ExtendedAdapter<GuestObservable, GuestViewHolder> {

    public GuestAdapter() {
        super();
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemGuestBinding binding = RecyclerViewItemGuestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GuestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        if (baseObservables != null) {
            RecyclerViewItemGuestBinding binding = RecyclerViewItemGuestBinding.bind(holder.itemView);
            GuestObservable guestObservable = baseObservables.get(position);
        }
    }

}
