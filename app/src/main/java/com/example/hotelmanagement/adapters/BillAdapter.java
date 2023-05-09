package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hotelmanagement.databinding.ListItemSampleBillBinding;
import com.example.hotelmanagement.observable.BillObservable;
import com.example.hotelmanagement.viewholders.BillViewHolder;

import java.util.List;

public class BillAdapter extends RecyclerView.Adapter<BillViewHolder> {

    public List<BillObservable> billObservables;

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSampleBillBinding binding = ListItemSampleBillBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BillViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        ListItemSampleBillBinding binding = ListItemSampleBillBinding.bind(holder.itemView);
        BillObservable billObservable = billObservables.get(position);
    }

    @Override
    public int getItemCount() {
        return billObservables.size();
    }

}
