package com.example.hotelmanagement.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.RecyclerViewItemBillBinding;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewholders.BillViewHolder;
import com.example.hotelmanagement.viewmodels.GuestViewModel;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class BillAdapter extends ExtendedAdapter<BillObservable, BillViewHolder> {

    private final BillListener billListener;

    public BillAdapter(FragmentActivity fragmentActivity, BillListener billListener) {
        super(fragmentActivity);
        this.billListener = billListener;
    }

    public BillAdapter(FragmentActivity fragmentActivity, BillListener billListener, List<BillObservable> billObservables) {
        super(fragmentActivity, billObservables);
        this.billListener = billListener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerViewItemBillBinding binding = RecyclerViewItemBillBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BillViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {

        RecyclerViewItemBillBinding binding = RecyclerViewItemBillBinding.bind(holder.itemView);
        BillObservable billObservable = items.get(position);

        binding.itemBillIdTextView.setText(String.valueOf(billObservable.getId()));
        binding.itemBillCostTextView.setText(String.valueOf(billObservable.getCost()));
        binding.itemBillStatusTextView.setText(billObservable.getIsPaid() ? "paid" : "unpaid");
        binding.itemBillCreatedAtTextView.setText(billObservable.getCreatedAt().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        if (billObservable.getIsPaid()) {
            binding.itemBill.setBackground(AppCompatResources.getDrawable(
                    fragmentActivity, R.drawable.rectangle_bg_indigo_500_border_indigo_100_radius_20_v1));
        }

        GuestViewModel guestViewModel = new ViewModelProvider(fragmentActivity).get(GuestViewModel.class);
        List<GuestObservable> guestObservables = guestViewModel.getModelState().getValue();
        Optional<GuestObservable> optionalGuestObservable = Optional.empty();
        if (guestObservables != null) {
            optionalGuestObservable = guestObservables.stream().filter
                    (guestObservable -> guestObservable.getId().equals(billObservable.getGuestId())).findFirst();
        }
        if (optionalGuestObservable.isPresent()) {
            GuestObservable guestObservable = optionalGuestObservable.get();
            binding.itemBillGuestNameTextView.setText
                    (guestObservable.getName());
            binding.itemBillIdNumberTextView.setText
                    (guestObservable.getIdNumber());
        } else {
            binding.itemBillGuestNameTextView.setText
                    ("                       ");
            binding.itemBillIdNumberTextView.setText
                    ("                           ");
        }

        binding.itemBillEditButton.setOnClickListener(view -> billListener.onEditBillClick(billObservable));
        binding.itemBillDeleButton.setOnClickListener(view -> billListener.onDeleBillClick(billObservable));

    }

    public interface BillListener {

        void onJustBillClick(BillObservable billObservable);

        void onEditBillClick(BillObservable billObservable);

        void onDeleBillClick(BillObservable billObservable);

    }

}
