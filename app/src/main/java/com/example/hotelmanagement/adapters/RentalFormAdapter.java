package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemRentalFormBinding;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.viewholders.RentalFormViewHolder;

public class RentalFormAdapter extends ExtendedAdapter<RentalFormObservable, RentalFormViewHolder> {

    public RentalFormAdapter() {
        super();
    }

    @NonNull
    @Override
    public RentalFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemRentalFormBinding binding = RecyclerViewItemRentalFormBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RentalFormViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalFormViewHolder holder, int position) {
        if (baseObservables != null) {
            RecyclerViewItemRentalFormBinding binding = RecyclerViewItemRentalFormBinding.bind(holder.itemView);
            RentalFormObservable rentalFormObservable = baseObservables.get(position);
        }
    }

}
