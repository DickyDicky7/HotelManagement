package com.example.hotelmanagement.adapters_viewmodels;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemRentalFormBinding;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.viewholders.RentalFormViewHolder;

import java.util.List;

public class RentalFormAdapterViewModel extends ExtendedAdapterViewModel<RentalFormObservable, RentalFormViewHolder> {

    public RentalFormAdapterViewModel() {
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
        List<RentalFormObservable> rentalFormObservables = modelState.getValue();
        if (rentalFormObservables != null) {
            RecyclerViewItemRentalFormBinding binding = RecyclerViewItemRentalFormBinding.bind(holder.itemView);
            RentalFormObservable rentalFormObservable = rentalFormObservables.get(position);
        }
    }

}
