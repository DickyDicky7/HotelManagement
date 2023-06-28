package com.example.hotelmanagement.fragments.adds;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAddRentalFormBinding;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentAddRentalForm extends Fragment {

    private FragmentAddRentalFormBinding binding;
    private RentalFormViewModel rentalFormViewModel;
    private RentalFormObservable rentalFormObservable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRentalFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rentalFormViewModel = new ViewModelProvider(requireActivity()).get(RentalFormViewModel.class);
        rentalFormObservable = new RentalFormObservable();
        binding.setRentalFormObservable(rentalFormObservable);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, new ArrayList<String>());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerChooseRoom.setAdapter(arrayAdapter);

        RoomViewModel roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        roomViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomObservables -> {
            arrayAdapter.clear();
            arrayAdapter.addAll(updatedRoomObservables.stream().filter(roomObservable -> !roomObservable.getIsOccupied()).map(RoomObservable::getName).toArray(String[]::new));

            binding.spinnerChooseRoom.setSelection(0, true);
            List<RoomObservable> isOccupiedFalseRoomObservables = updatedRoomObservables.stream().filter
                    (roomObservable -> !roomObservable.getIsOccupied()).collect(Collectors.toList());
            if (!isOccupiedFalseRoomObservables.isEmpty()) {
                RoomObservable selectedRoomObservable = isOccupiedFalseRoomObservables.get(binding.spinnerChooseRoom.getSelectedItemPosition());
                rentalFormObservable.setRoomId(selectedRoomObservable.getId());
            }

        });

        binding.radioResolved.setEnabled(false);
        rentalFormObservable.setIsResolved(false);

        binding.edtIdNumber.setOnFocusChangeListener((_view_, b) -> {
            if (!b) {
                rentalFormViewModel.findGuestId(rentalFormObservable);
            }
        });

        binding.edtRentalDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (rentalFormObservable.getPricePerDay() != null && rentalFormObservable.getRentalDays() != null) {
                    rentalFormObservable.setAmount(rentalFormObservable.getPricePerDay() * rentalFormObservable.getRentalDays());
                    rentalFormObservable.notifyPropertyChanged(BR.amountString);
                }
            }
        });

        binding.edtStartDate.setOnClickListener(_view_ -> {
            Calendar currentDate = Calendar.getInstance();
            int y = currentDate.get(Calendar.YEAR);
            int m = currentDate.get(Calendar.MONTH);
            int d = currentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog startDatePicker = new DatePickerDialog(requireActivity(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                LocalDate startDate = LocalDate.of(selectedYear, selectedMonth, selectedDay);
                rentalFormObservable.setStartDateString(startDate.toString());
            }, y, m, d);
            startDatePicker.setTitle("Select Start Date");
            startDatePicker.show();
        });

        binding.edtPricePerDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (rentalFormObservable.getPricePerDay() != null && rentalFormObservable.getRentalDays() != null) {
                    rentalFormObservable.setAmount(rentalFormObservable.getPricePerDay() * rentalFormObservable.getRentalDays());
                    rentalFormObservable.notifyPropertyChanged(BR.amountString);
                }
            }
        });

        binding.edtNumberOfGuests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (rentalFormObservable.getNumberOfGuestsString() == null ||
                        rentalFormObservable.getNumberOfGuestsString().equals("")) {
                    return;
                }
                rentalFormViewModel.findPrice(rentalFormObservable);
            }
        });

        binding.spinnerChooseRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue();
                if (roomObservables != null) {
                    roomObservables = roomObservables.stream().filter(roomObservable -> !roomObservable.getIsOccupied()).collect(Collectors.toList());
                    rentalFormObservable.setRoomId(roomObservables.get(i).getId());
                    if (rentalFormObservable.getNumberOfGuestsString() == null || rentalFormObservable.getNumberOfGuestsString().equals("")) {
                        return;
                    }
                    rentalFormViewModel.findPrice(rentalFormObservable);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                if (binding.edtIdNumber.isFocused()) {
                    Toast.makeText(requireActivity(), "Finish Entering Id Number To Continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                rentalFormViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            rentalFormObservable = new RentalFormObservable();
                            rentalFormObservable.setIsResolved(false);
                            binding.radioResolved.setEnabled(false);
                            binding.setRentalFormObservable(rentalFormObservable);
                        });
                    }
                };
                rentalFormViewModel.onFailureCallback = null;
                if (rentalFormViewModel.checkObservable(rentalFormObservable, requireContext(), "billId")) {
                    rentalFormObservable.setBillId(null);
                    rentalFormObservable.setIsResolved(false);
                    rentalFormViewModel.insert(rentalFormObservable);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            View currentFocusView = requireActivity().getCurrentFocus();
            if (currentFocusView != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        rentalFormViewModel = null;
        rentalFormObservable = null;
    }

}
