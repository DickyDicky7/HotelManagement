package com.example.hotelmanagement.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.databinding.RecyclerViewItemRentalFormBinding;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.viewholders.RentalFormViewHolder;
import com.example.hotelmanagement.viewmodels.GuestViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class RentalFormAdapter extends ExtendedAdapter<RentalFormObservable, RentalFormViewHolder> {

    public RentalFormAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public RentalFormAdapter(FragmentActivity fragmentActivity, List<RentalFormObservable> rentalFormObservables) {
        super(fragmentActivity, rentalFormObservables);
    }

    @NonNull
    @Override
    public RentalFormViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemRentalFormBinding binding = RecyclerViewItemRentalFormBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RentalFormViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RentalFormViewHolder holder, int position) {
        if (baseObservables != null) {

            RecyclerViewItemRentalFormBinding binding = RecyclerViewItemRentalFormBinding.bind(holder.itemView);
            RentalFormObservable rentalFormObservable = baseObservables.get(position);

            binding.itemRentFormCode2.setText("#" + rentalFormObservable.getId());
            binding.itemRentFormName2.setText("Rental Form " + rentalFormObservable.getId());
            LocalDate startDate = rentalFormObservable.getStartDate();
            LocalDate endDate = startDate.plusDays(rentalFormObservable.getRentalDays());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            binding.beginEndDate.setText(startDate.format(dateTimeFormatter) + " - " + endDate.format(dateTimeFormatter));

            GuestViewModel guestViewModel = new ViewModelProvider(fragmentActivity).get(GuestViewModel.class);
            List<GuestObservable> guestObservables = guestViewModel.getModelState().getValue();
            Optional<GuestObservable> optionalGuestObservable = Optional.empty();
            if (guestObservables != null) {
                optionalGuestObservable = guestObservables.stream().filter(guestObservable -> guestObservable.getId().equals(rentalFormObservable.getGuestId())).findFirst();
            }
            if (optionalGuestObservable.isPresent()) {
                GuestObservable guestObservable = optionalGuestObservable.get();
                binding.customerName.setText(guestObservable.getName());
            } else {
                binding.customerName.setText("");
            }

        }
    }

}
