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

import com.example.hotelmanagement.databinding.FragmentAddRoomBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.ExtendedViewModel;
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
        roomViewModel = ExtendedViewModel.getViewModel(requireActivity(), RoomViewModel.class);
        roomObservable = new RoomObservable();
        RoomKindViewModel roomKindViewModel = ExtendedViewModel.getViewModel(requireActivity(), RoomKindViewModel.class);
        List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState().getValue();
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i = 0; i < roomKindObservables.size(); i++)
            arrayList.add(roomKindObservables.get(i).getName());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        binding.spinner.setAdapter(arrayAdapter);
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                roomObservable.setRoomKindId(roomKindObservables.get(i).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnDone.setOnClickListener(_view_ -> roomViewModel.checkObservable(roomObservable));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomObservable = null;
        roomViewModel = null;
    }

}
