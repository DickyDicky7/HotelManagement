package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.databinding.RecyclerViewItemBillBinding;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewholders.BillViewHolder;
import com.example.hotelmanagement.viewmodels.GuestViewModel;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class BillAdapter extends ExtendedAdapter<BillObservable, BillViewHolder> {

    public BillAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public BillAdapter(FragmentActivity fragmentActivity, List<BillObservable> billObservables) {
        super(fragmentActivity, billObservables);
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

            binding.costTextView.setText(String.valueOf(billObservable.getCost()));
            binding.billIdTextView.setText(String.valueOf(billObservable.getId()));
            binding.statusTextView.setText(billObservable.getIsPaid() ? "paid" : "unpaid");
            binding.createdAtTextView.setText(billObservable.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            GuestViewModel guestViewModel = new ViewModelProvider(fragmentActivity).get(GuestViewModel.class);
            List<GuestObservable> guestObservables = guestViewModel.getModelState().getValue();
            Optional<GuestObservable> optionalGuestObservable = Optional.empty();
            if (guestObservables != null) {
                optionalGuestObservable = guestObservables.stream().filter(guestObservable -> guestObservable.getId().equals(billObservable.getGuestId())).findFirst();
            }
            if (optionalGuestObservable.isPresent()) {
                GuestObservable guestObservable = optionalGuestObservable.get();
                binding.idNumberTextView.setText(guestObservable.getIdNumber());
                binding.guestNameTextView.setText(guestObservable.getName());
            } else {
                binding.idNumberTextView.setText("");
                binding.guestNameTextView.setText("");
            }

        }
    }

}
