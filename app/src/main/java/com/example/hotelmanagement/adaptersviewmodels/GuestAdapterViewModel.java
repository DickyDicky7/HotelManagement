package com.example.hotelmanagement.adaptersviewmodels;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.databinding.ListItemSampleCustomerBinding;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewholders.GuestViewHolder;

import java.util.List;

public class GuestAdapterViewModel extends ExtendedAdapterViewModel<GuestObservable, GuestViewHolder> {

    public GuestAdapterViewModel() {
        super();
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemSampleCustomerBinding binding = ListItemSampleCustomerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GuestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        List<GuestObservable> guestObservables = modelState.getValue();
        if (guestObservables != null) {
            ListItemSampleCustomerBinding binding = ListItemSampleCustomerBinding.bind(holder.itemView);
            GuestObservable guestObservable = guestObservables.get(position);
        }
    }

}
