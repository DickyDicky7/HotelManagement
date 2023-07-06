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
import androidx.navigation.fragment.NavHostFragment;

import com.example.common.Common;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAddRoomBinding;
import com.example.hotelmanagement.dialogs.DialogFragmentFailure;
import com.example.hotelmanagement.dialogs.DialogFragmentSuccess;
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

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        binding.spinnerChooseRoomKind.setAdapter(arrayAdapter);

        RoomKindViewModel roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomKindObservables -> {
            arrayAdapter.clear();
            arrayAdapter.addAll(updatedRoomKindObservables.stream().map(RoomKindObservable::getName).toArray(String[]::new));
            binding.spinnerChooseRoomKind.setSelection(arrayAdapter.getPosition
                    (roomKindViewModel.getRoomKindName(roomObservable.getRoomKindId())), true);
        });

        binding.spinnerChooseRoomKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
                roomViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                DialogFragmentFailure.newOne(getParentFragmentManager()
                                        , "FragmentAddRoom Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                roomViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            roomObservable = new RoomObservable();
                            binding.setRoomObservable(roomObservable);
                            List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState().getValue();
                            if (roomKindObservables != null) {
                                roomObservable.setRoomKindId(roomKindObservables.get(
                                        binding.spinnerChooseRoomKind.getSelectedItemPosition()).getId());
                            }
                            roomObservable.setIsOccupied(false);
                            String message = "Success: Your item has been added successfully.";
                            DialogFragmentSuccess.newOne(getParentFragmentManager()
                                    , "FragmentAddRoom Success", message);
                        });
                    }
                };
                roomViewModel.onFailureCallback = null;
                if (roomViewModel.checkObservable(roomObservable, requireContext(), binding.getRoot(), "note", "description")) {
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
            Common.hideKeyboard(requireActivity());
        });

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomViewModel = null;
        roomObservable = null;
    }

}
