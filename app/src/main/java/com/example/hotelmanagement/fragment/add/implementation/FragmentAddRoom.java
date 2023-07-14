package com.example.hotelmanagement.fragment.add.implementation;

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

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentAddRoomBinding;
import com.example.hotelmanagement.dialog.watcher.DialogFragmentWatcher;
import com.example.hotelmanagement.observable.implementation.RoomKindObservable;
import com.example.hotelmanagement.observable.implementation.RoomObservable;
import com.example.hotelmanagement.popupwindow.common.implementation.PopupWindowLoading;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RoomViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddRoom extends Fragment {

    @Nullable
    private PopupWindowLoading popupWindowLoading;

    private FragmentAddRoomBinding binding;
    private RoomViewModel roomViewModel;
    private RoomObservable roomObservable;

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
                    String failureTag = "FragmentAddRoom Failure";
                    DialogFragmentWatcher.dialogFragmentFailureSubscribe(failureTag, Common.getFailureMessage(
                            apolloErrors, apolloException, cloudinaryErrorInfo));
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (popupWindowLoading != null) {
                                popupWindowLoading.dismiss();
                            }
                        });
                    }
                };
                roomViewModel.onSuccessCallback = () -> {
                    String successTag = "FragmentAddRoom Success";
                    DialogFragmentWatcher.dialogFragmentSuccessSubscribe(successTag, Common.getSuccessMessage(
                            Common.Action.INSERT_ITEM));
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
                            if (popupWindowLoading != null) {
                                popupWindowLoading.dismiss();
                            }
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
                    popupWindowLoading = PopupWindowLoading.newOne(getLayoutInflater(), binding.linearAddRoom);
                    popupWindowLoading.showAsDropDown
                            (binding.linearAddRoom);
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
        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
    }

}
