package com.example.hotelmanagement.fragment.detail.implementation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.databinding.FragmentDetailRoomKindBinding;
import com.example.hotelmanagement.observable.implementation.RoomKindObservable;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;

public class FragmentDetailRoomKind extends Fragment {

    private FragmentDetailRoomKindBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailRoomKindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RoomKindViewModel roomKindViewModel
                = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        RoomKindObservable roomKindObservable
                = roomKindViewModel.getObservable(requireArguments().getInt("id"));

        if (roomKindObservable != null) {
            Glide.with(this).load(roomKindObservable.getImageURL()).into(binding.imageImageThirteen);
            binding.txtPrice.setText(roomKindObservable.getPrice().toString() + " / day");
            binding.txtPriceOne.setText(roomKindObservable.getNumberOfBeds() + " bed | " + roomKindObservable.getCapacity() + " people");
            binding.txtSingleRoom.setText(roomKindObservable.getName());

        }

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
