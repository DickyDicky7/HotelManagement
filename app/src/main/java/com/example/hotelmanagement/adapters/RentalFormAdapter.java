package com.example.hotelmanagement.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.R;
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

    private final RentalFormListener rentalFormListener;

    public RentalFormAdapter(FragmentActivity fragmentActivity, RentalFormListener rentalFormListener) {
        super(fragmentActivity);
        this.rentalFormListener = rentalFormListener;
    }

    public RentalFormAdapter(FragmentActivity fragmentActivity, RentalFormListener rentalFormListener, List<RentalFormObservable> rentalFormObservables) {
        super(fragmentActivity, rentalFormObservables);
        this.rentalFormListener = rentalFormListener;

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

        RecyclerViewItemRentalFormBinding binding = RecyclerViewItemRentalFormBinding.bind(holder.itemView);
        RentalFormObservable rentalFormObservable = items.get(position);

        binding.itemRentalFormCodeTextView.setText("#" + rentalFormObservable.getId());
        binding.itemRentalFormNameTextView.setText("Rental Form " + rentalFormObservable.getId());
        LocalDate startDate = rentalFormObservable.getStartDate();
        LocalDate endDate = startDate.plusDays(rentalFormObservable.getRentalDays());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        binding.itemRentalFormBeginEndDateTextView.setText(startDate.format(dateTimeFormatter) + " \u279E " + endDate.format(dateTimeFormatter));
        if (rentalFormObservable.getIsResolved()) {
            binding.itemRentalForm.setBackground(AppCompatResources.getDrawable(
                    fragmentActivity, R.drawable.rectangle_bg_indigo_500_border_indigo_100_radius_20));
        }

        GuestViewModel guestViewModel = new ViewModelProvider(fragmentActivity).get(GuestViewModel.class);
        List<GuestObservable> guestObservables = guestViewModel.getModelState().getValue();
        Optional<GuestObservable> optionalGuestObservable = Optional.empty();
        if (guestObservables != null) {
            optionalGuestObservable = guestObservables.stream().filter
                    (guestObservable -> guestObservable.getId().equals(rentalFormObservable.getGuestId())).findFirst();
        }
        if (optionalGuestObservable.isPresent()) {
            GuestObservable guestObservable = optionalGuestObservable.get();
            binding.itemRentalFormGuestNameTextView.setText(guestObservable.getName());
        } else {
            binding.itemRentalFormGuestNameTextView.setText("");
        }

        binding.itemRentalFormEditButton.setOnClickListener(view -> rentalFormListener.onEditRentalFormClick(rentalFormObservable));
        binding.itemRentalFormDeleButton.setOnClickListener(view -> rentalFormListener.onDeleRentalFormClick(rentalFormObservable));

    }

    public interface RentalFormListener {

        void onJustRentalFormClick(RentalFormObservable rentalFormObservable);

        void onEditRentalFormClick(RentalFormObservable rentalFormObservable);

        void onDeleRentalFormClick(RentalFormObservable rentalFormObservable);

    }

}
