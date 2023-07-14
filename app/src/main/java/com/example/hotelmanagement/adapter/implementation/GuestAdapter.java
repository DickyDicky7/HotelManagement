package com.example.hotelmanagement.adapter.implementation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.example.hotelmanagement.adapter.abstraction.ExtendedAdapter;
import com.example.hotelmanagement.adapter.viewholder.implementation.GuestViewHolder;
import com.example.hotelmanagement.databinding.RecyclerViewItemGuestBinding;
import com.example.hotelmanagement.observable.implementation.GuestObservable;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class GuestAdapter extends ExtendedAdapter<GuestObservable, GuestViewHolder> {

    private final GuestListener guestListener;

    public GuestAdapter(FragmentActivity fragmentActivity, GuestListener guestListener) {
        super(fragmentActivity);
        this.guestListener = guestListener;
    }

    public GuestAdapter(FragmentActivity fragmentActivity, GuestListener guestListener, List<GuestObservable> guestObservables) {
        super(fragmentActivity, guestObservables);
        this.guestListener = guestListener;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemGuestBinding binding = RecyclerViewItemGuestBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new GuestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {

        RecyclerViewItemGuestBinding binding = RecyclerViewItemGuestBinding.bind(holder.itemView);
        GuestObservable guestObservable = items.get(position);

        binding.itemGuestNameTextView.setText(guestObservable.getName());
        binding.itemGuestAddressTextView.setText(guestObservable.getAddress());
        binding.itemGuestIdNumberTextView.setText(guestObservable.getIdNumber());
        binding.itemGuestCreatedAtTextView.setText(guestObservable.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        binding.itemGuestPhoneNumberTextView.setText(guestObservable.getPhoneNumber());

        binding.itemGuestEditButton.setOnClickListener(view -> guestListener.onEditGuestClick(guestObservable));
        binding.itemGuestDeleButton.setOnClickListener(view -> guestListener.onDeleGuestClick(guestObservable));

    }

    public interface GuestListener {

        void onJustGuestClick(GuestObservable guestObservable);

        void onEditGuestClick(GuestObservable guestObservable);

        void onDeleGuestClick(GuestObservable guestObservable);

    }

}
