package com.example.hotelmanagement.fragments.adds;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelmanagement.databinding.FragmentAddBillBinding;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.example.hotelmanagement.viewmodels.ExtendedViewModel;

public class FragmentAddBill extends Fragment {

    private FragmentAddBillBinding binding;
    private BillObservable billObservable;
    private BillViewModel billViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        billObservable = new BillObservable();
        billViewModel = ExtendedViewModel.getViewModel(requireActivity(), BillViewModel.class);
        binding.setBillObservable(billObservable);
        binding.edtIdNumber.setOnFocusChangeListener((_view_, b) -> {
            billViewModel.findGuestId(billObservable);
        });
        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.edtIdNumber.isFocused()) {
                    Toast.makeText(requireActivity(), "Finish entering id number to process", Toast.LENGTH_SHORT).show();
                    return;
                }
                billViewModel.calculateCost(billObservable);
            }
        });

        binding.btnDone.setOnClickListener(_view_ -> {
            billViewModel.checkObservable(billObservable);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        billObservable = null;
        billViewModel = null;
    }

}
