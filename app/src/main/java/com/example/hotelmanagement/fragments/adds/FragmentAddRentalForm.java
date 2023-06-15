package com.example.hotelmanagement.fragments.adds;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
        });
        binding.radioResolved.setEnabled(false);
        rentalFormObservable.setIsResolved(false);
        binding.edtIDnumber.setOnFocusChangeListener((_view_, b) -> {
            if (!b) {
                rentalFormViewModel.findGuestId(rentalFormObservable);
            }
        });

        binding.etRentalDay.addTextChangedListener(new TextWatcher() {
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

        binding.edtDate.setOnClickListener(_view_ -> {
            Calendar currentDate = Calendar.getInstance();
            int y = currentDate.get(Calendar.YEAR);
            int m = currentDate.get(Calendar.MONTH);
            int d = currentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(requireActivity()
                    , (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                LocalDate date = LocalDate.of(selectedYear, selectedMonth, selectedDay);
                rentalFormObservable.setStartDateString(date.toString());
            }, y, m, d);
            mDatePicker.setTitle("Select Date");
            mDatePicker.show();
        });
        binding.etPricePerDay.addTextChangedListener(new TextWatcher() {
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

        binding.edtNumOfGuest.addTextChangedListener(new TextWatcher() {
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
                List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue().stream().filter(roomObservable -> !roomObservable.getIsOccupied()).collect(Collectors.toList());
                if (roomObservables != null) {
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
                if (binding.edtIDnumber.isFocused()) {
                    Toast.makeText(requireActivity(), "Finish Entering Id Number To Continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                rentalFormViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            rentalFormObservable = new RentalFormObservable();
                            binding.setRentalFormObservable(rentalFormObservable);
                            List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue().stream().filter(roomObservable -> !roomObservable.getIsOccupied()).collect(Collectors.toList());
                            rentalFormObservable.setRoomId(roomObservables.get(binding.spinnerChooseRoom.getSelectedItemPosition()).getId());
                            rentalFormObservable.setIsResolved(false);
                        });
                    }
                };
                rentalFormViewModel.onFailureCallback = null;
                if (rentalFormViewModel.checkObservable(rentalFormObservable, requireContext(), "billId")) {
                    rentalFormObservable.setBillId(null);
                    //rentalFormObservable.setIsResolved(false);
                    rentalFormViewModel.insert(rentalFormObservable);
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
        rentalFormViewModel = null;
        rentalFormObservable = null;
    }

}
