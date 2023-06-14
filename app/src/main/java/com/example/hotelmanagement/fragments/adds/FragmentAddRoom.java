package com.example.hotelmanagement.fragments.adds;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAddRoomBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddRoom extends Fragment {

    private FragmentAddRoomBinding binding;
    private RoomObservable roomObservable;
    private RoomViewModel roomViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        roomObservable = new RoomObservable();
        binding.setRoomObservable(roomObservable);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, new ArrayList<String>());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinner.setAdapter(arrayAdapter);

        RoomKindViewModel roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomKindObservables -> {
            arrayAdapter.addAll(updatedRoomKindObservables.stream().map(RoomKindObservable::getName).toArray(String[]::new));
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState().getValue();
                if (roomKindObservables != null) {
                    roomObservable.setRoomKindId(roomKindObservables.get(i).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                roomObservable.setRoomKindId(null);
            }
        });

        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                roomViewModel.onSuccessCallback = () -> {
                    requireActivity().runOnUiThread(() -> {
                        roomObservable = new RoomObservable();
                        binding.setRoomObservable(roomObservable);
                    });
                };
                roomViewModel.onFailureCallback = null;
                if (roomViewModel.checkObservable(roomObservable, requireContext(), "note", "description")) {
                    if (roomObservable.getNote() != null && roomObservable.getNote().equals("")) {
                        roomObservable.setNote(null);
                    }
                    if (roomObservable.getDescription() != null && roomObservable.getDescription().equals("")) {
                        roomObservable.setDescription(null);
                    }
                    roomViewModel.insert(roomObservable);
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
        roomViewModel = null;
        roomObservable = null;
    }

}
