package com.example.hotelmanagement.fragments.edits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.databinding.FragmentEditBillBinding;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;

public class FragmentEditBill extends Fragment {

    private FragmentEditBillBinding binding;

    private BillObservable billObservable;
    private BillViewModel billViewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        billObservable = new BillObservable();
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
        int id = getArguments().getInt("id");
        billViewModel.filldata(billObservable, id);
        while (billObservable.getId() == null);
        billViewModel.GuestQueryById(billObservable);
        while (billObservable.getIdNumber() == null);
        binding.setBillObservable(billObservable);

        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                billViewModel.onSuccessCallback = () -> {
                };
                billViewModel.onFailureCallback = null;
                if (billViewModel.checkObservable(billObservable, requireContext())) {
                    billViewModel.update(billObservable,id);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        billViewModel = null;
        billObservable = null;
    }

}