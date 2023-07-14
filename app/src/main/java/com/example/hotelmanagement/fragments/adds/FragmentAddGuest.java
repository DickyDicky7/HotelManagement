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

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentAddGuestBinding;
import com.example.hotelmanagement.dialog.watcher.DialogFragmentWatcher;
import com.example.hotelmanagement.observables.GuestKindObservable;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.popupwindow.implementation.PopupWindowLoading;
import com.example.hotelmanagement.viewmodel.implementation.GuestKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.GuestViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddGuest extends Fragment {

    @Nullable
    private PopupWindowLoading popupWindowLoading;

    private FragmentAddGuestBinding binding;
    private GuestViewModel guestViewModel;
    private GuestObservable guestObservable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddGuestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestViewModel = new ViewModelProvider(requireActivity()).get(GuestViewModel.class);
        guestObservable = new GuestObservable();
        binding.setGuestObservable(guestObservable);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        binding.spinnerChooseGuestKind.setAdapter(arrayAdapter);

        GuestKindViewModel guestKindViewModel = new ViewModelProvider(requireActivity()).get(GuestKindViewModel.class);
        guestKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedGuestKindObservables -> {
            arrayAdapter.clear();
            arrayAdapter.addAll(updatedGuestKindObservables.stream().map(GuestKindObservable::getName).toArray(String[]::new));
            binding.spinnerChooseGuestKind.setSelection(arrayAdapter.getPosition(
                    guestKindViewModel.getGuestKindName(guestObservable.getGuestKindId())), true);
        });

        binding.spinnerChooseGuestKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<GuestKindObservable> guestKindObservables = guestKindViewModel.getModelState().getValue();
                if (guestKindObservables != null) {
                    guestObservable.setGuestKindId(guestKindObservables.get(i).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                guestObservable.setGuestKindId(null);
            }
        });

        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                guestViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    String failureTag = "FragmentAddGuest Failure";
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
                guestViewModel.onSuccessCallback = () -> {
                    String successTag = "FragmentAddGuest Success";
                    DialogFragmentWatcher.dialogFragmentSuccessSubscribe(successTag, Common.getSuccessMessage(
                            Common.Action.INSERT_ITEM));
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            guestObservable = new GuestObservable();
                            binding.setGuestObservable(guestObservable);
                            List<GuestKindObservable> guestKindObservables = guestKindViewModel.getModelState().getValue();
                            if (guestKindObservables != null) {
                                guestObservable.setGuestKindId(guestKindObservables.get(
                                        binding.spinnerChooseGuestKind.getSelectedItemPosition()).getId());
                            }
                            if (popupWindowLoading != null) {
                                popupWindowLoading.dismiss();
                            }
                        });
                    }
                };
                guestViewModel.onFailureCallback = null;
                if (guestViewModel.checkObservable(guestObservable, requireContext(), binding.getRoot())) {
                    guestViewModel.insert(guestObservable);
                    popupWindowLoading = PopupWindowLoading.newOne(getLayoutInflater(), binding.linearAddGuest);
                    popupWindowLoading.showAsDropDown
                            (binding.linearAddGuest);
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
        guestViewModel = null;
        guestObservable = null;
        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
    }

}
