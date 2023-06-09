package com.example.hotelmanagement.fragments.adds;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.databinding.FragmentAddRoomKindBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;

public class FragmentAddRoomKind extends Fragment {

    private FragmentAddRoomKindBinding binding;
    private RoomKindViewModel roomKindViewModel;
    private RoomKindObservable roomKindObservable;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    roomKindObservable.setImageURL(uri.toString());
                    binding.edtImage.setColorFilter(Color.TRANSPARENT);
                    Glide.with(this).load(uri).centerInside().into(binding.edtImage);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRoomKindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindObservable = new RoomKindObservable();
        binding.setRoomKindObservable(roomKindObservable);
        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                roomKindViewModel.onSuccessCallback = unused -> {
                    requireActivity().runOnUiThread(() -> {
                    });
                    //roomKindObservable = new RoomKindObservable();
                };
                roomKindViewModel.onFailureCallback = null;
                if (roomKindViewModel.checkObservable(roomKindObservable, requireContext())) {
                    roomKindViewModel.insert(roomKindObservable);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        binding.edtImage.setColorFilter(Color.WHITE);
        binding.edtImage.setOnClickListener(_view_ -> {
            pickVisualMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomKindViewModel = null;
        roomKindObservable = null;
        pickVisualMediaLauncher.unregister();
        pickVisualMediaLauncher = null;
    }

}
