package com.example.hotelmanagement.fragments.adds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelmanagement.databinding.FragmentAddGuestBinding;
import com.example.hotelmanagement.observables.GuestKindObservable;
import com.example.hotelmanagement.viewmodels.ExtendedViewModel;
import com.example.hotelmanagement.viewmodels.GuestKindViewModel;

import java.util.List;

public class FragmentAddGuest extends Fragment {

    private FragmentAddGuestBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddGuestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GuestKindViewModel guestKindViewModel = ExtendedViewModel.getViewModel(requireActivity(), GuestKindViewModel.class);
        List<GuestKindObservable> guestKindObservables = guestKindViewModel.getModelState().getValue();
        System.out.println("Number of guest kind observables: " + guestKindObservables.size());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
