package com.example.hotelmanagement.fragments.adds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAddRoomKindBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodels.ExtendedViewModel;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;

public class FragmentAddRoomKind extends Fragment {

    private FragmentAddRoomKindBinding binding;

    private RoomKindViewModel roomKindViewModel;
    private RoomKindObservable roomKindObservable;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRoomKindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomKindViewModel = ExtendedViewModel.getViewModel(requireActivity(), RoomKindViewModel.class);
        roomKindObservable = new RoomKindObservable();
        binding.setRoomKindObservable(roomKindObservable);
        binding.btnDone.setOnClickListener(_view_ -> {
            roomKindViewModel.onSuccessCallback = unused -> {
                requireActivity().runOnUiThread(() -> {
                    NavHostFragment.findNavController(this).navigate(R.id.action_fragmentAddRoomKind_to_fragmentTempHome);
                });
                //roomKindObservable = new RoomKindObservable();
            };
            roomKindViewModel.onFailureCallback = null;
            roomKindViewModel.checkObservable(roomKindObservable);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomKindObservable = null;
        roomKindViewModel = null;
    }

}
