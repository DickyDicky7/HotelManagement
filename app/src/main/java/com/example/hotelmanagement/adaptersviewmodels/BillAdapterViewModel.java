package com.example.hotelmanagement.adaptersviewmodels;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.ListItemSampleBillBinding;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.viewholders.BillViewHolder;

import java.util.List;

public class BillAdapterViewModel extends ExtendedAdapterViewModel<BillObservable, BillViewHolder> {

    public BillAdapterViewModel() {
        super();
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSampleBillBinding binding = ListItemSampleBillBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BillViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        List<BillObservable> billObservables = modelState.getValue();
        if (billObservables != null) {
            ListItemSampleBillBinding binding = ListItemSampleBillBinding.bind(holder.itemView);
            BillObservable billObservable = billObservables.get(position);
        }
    }

}
