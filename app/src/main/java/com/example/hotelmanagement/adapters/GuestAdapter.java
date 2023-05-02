package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.databinding.ListItemSampleCustomerBinding;
import com.example.hotelmanagement.observable.GuestObservable;
import com.example.hotelmanagement.viewholders.GuestViewHolder;

import java.util.List;

public class GuestAdapter extends RecyclerView.Adapter<GuestViewHolder> {

    public List<GuestObservable> guestObservables;

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSampleCustomerBinding binding = ListItemSampleCustomerBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new GuestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        ListItemSampleCustomerBinding binding = ListItemSampleCustomerBinding.bind(holder.itemView);
        GuestObservable guestObservable = guestObservables.get(position);
    }

    @Override
    public int getItemCount() {
        return guestObservables.size();
    }

}
