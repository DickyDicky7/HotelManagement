package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.RecyclerViewItemBillBinding;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.viewholders.BillViewHolder;

public class BillAdapter extends ExtendedAdapter<BillObservable, BillViewHolder> {

    public BillAdapter() {
        super();
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemBillBinding binding = RecyclerViewItemBillBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BillViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        if (baseObservables != null) {
            RecyclerViewItemBillBinding binding = RecyclerViewItemBillBinding.bind(holder.itemView);
            BillObservable billObservable = baseObservables.get(position);
        }
    }

}
