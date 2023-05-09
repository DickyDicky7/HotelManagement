package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.databinding.ListItemSampleRentformBinding;
import com.example.hotelmanagement.observable.RentalFormObservable;
import com.example.hotelmanagement.viewholders.RentalFormViewHolder;

import java.util.List;

public class RentalFormAdapter extends RecyclerView.Adapter<RentalFormViewHolder> {

    public List<RentalFormObservable> rentalFormObservables;

    @NonNull
    @Override
    public RentalFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSampleRentformBinding binding = ListItemSampleRentformBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RentalFormViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RentalFormViewHolder holder, int position) {
        ListItemSampleRentformBinding binding = ListItemSampleRentformBinding.bind(holder.itemView);
        RentalFormObservable rentalFormObservable = rentalFormObservables.get(position);
    }

    @Override
    public int getItemCount() {
        return rentalFormObservables.size();
    }

}
