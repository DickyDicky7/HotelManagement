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
import com.example.hotelmanagement.databinding.FragmentAddGuestBinding;
import com.example.hotelmanagement.observables.GuestKindObservable;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewmodels.GuestKindViewModel;
import com.example.hotelmanagement.viewmodels.GuestViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentAddGuest extends Fragment {

    private FragmentAddGuestBinding binding;
    private GuestObservable guestObservable;
    private GuestViewModel guestViewModel;

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

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, new ArrayList<String>());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerChooseGuestKind.setAdapter(arrayAdapter);

        GuestKindViewModel guestKindViewModel = new ViewModelProvider(requireActivity()).get(GuestKindViewModel.class);
        List<GuestKindObservable> guestKindObservables = guestKindViewModel.getModelState().getValue();
        guestKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedGuestKindObservables -> {
            arrayAdapter.clear();
            arrayAdapter.addAll(updatedGuestKindObservables.stream().map(GuestKindObservable::getName).toArray(String[]::new));
        });

        binding.spinnerChooseGuestKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
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
                guestViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            guestObservable = new GuestObservable();
                            binding.setGuestObservable(guestObservable);
                            guestObservable.setGuestKindId(guestKindObservables.get(binding.spinnerChooseGuestKind.getSelectedItemPosition()).getId());
                        });
                    }
                };
                guestViewModel.onFailureCallback = null;
                if (guestViewModel.checkObservable(guestObservable, requireContext())) {
                    guestViewModel.insert(guestObservable);
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
        guestViewModel = null;
        guestObservable = null;
    }

}
