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
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAddRentalFormBinding;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.ExtendedViewModel;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentAddRentalForm extends Fragment {

    private FragmentAddRentalFormBinding binding;

    private RentalFormObservable rentalFormObservable;
    private RentalFormViewModel rentalFormViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRentalFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rentalFormViewModel = ExtendedViewModel.getViewModel(requireActivity(), RentalFormViewModel.class);
        rentalFormObservable = new RentalFormObservable();
        binding.setRentalFormObservable(rentalFormObservable);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                requireContext(),
                R.layout.spinner_item,
                new ArrayList<String>());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerChooseRoom.setAdapter(arrayAdapter);

        RoomViewModel roomViewModel = ExtendedViewModel.getViewModel(requireActivity(), RoomViewModel.class);
        roomViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomObservable -> {
            arrayAdapter.addAll(updatedRoomObservable.stream().map(RoomObservable::getName).toArray(String[]::new));
        });
        binding.edtIDnumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
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
                }
            }
        });
        binding.imageCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentDate = Calendar.getInstance();
                int year = currentDate.get(Calendar.YEAR);
                int month = currentDate.get(Calendar.MONTH);
                int day = currentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog mDatePicker = new DatePickerDialog(requireActivity(), new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedYear, int selectedMonth, int selectedDay) {
                        rentalFormObservable.setStartDateString(selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay);
                    }
                }, year, month, day);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
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
                rentalFormViewModel.findPrice(rentalFormObservable);
            }
        });
        binding.spinnerChooseRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue();
                rentalFormObservable.setRoomId(roomObservables.get(i).getId());
                if (rentalFormObservable.getNumOfGuestsString() == null || rentalFormObservable.getNumOfGuestsString().equals("")) {
                    return;
                }
                rentalFormViewModel.findPrice(rentalFormObservable);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.edtIDnumber.isFocused()) {
                    Toast.makeText(requireActivity(), "Finish entering id number to process", Toast.LENGTH_SHORT).show();
                    return;
                }
                rentalFormViewModel.checkObservable(rentalFormObservable);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
