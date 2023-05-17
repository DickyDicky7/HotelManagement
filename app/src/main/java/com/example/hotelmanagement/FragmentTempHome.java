package com.example.hotelmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.databinding.FragmentTempHomeBinding;

public class FragmentTempHome extends Fragment {

    private FragmentTempHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTempHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.button.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddRoom);
        });

        binding.button2.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddGuest);
        });

        binding.button3.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddRoomKind);
        });

        binding.button4.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddBill);
        });

        binding.button5.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddRentalForm);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}