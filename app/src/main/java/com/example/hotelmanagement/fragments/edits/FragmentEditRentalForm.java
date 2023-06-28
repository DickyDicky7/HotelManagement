package com.example.hotelmanagement.fragments.edits;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentEditRentalFormBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;
import com.example.hotelmanagement.dialog.SuccessDialogFragment;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class FragmentEditRentalForm extends Fragment {

    private FragmentEditRentalFormBinding binding;
    private BillViewModel billViewModel;
    private BillObservable billObservable;
    private RentalFormViewModel rentalFormViewModel;
    private RentalFormObservable usedRentalFormObservable;
    private RentalFormObservable copyRentalFormObservable;

    private Handler handler = new Handler(message -> {
        if (getContext() == null) {
            return false;
        }
        int gray = Color.GRAY;
        int indigo = requireContext().getColor(R.color.indigo_400);
        ValueAnimator grayToIndigoAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), gray, indigo);
        ValueAnimator indigoToGrayAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), indigo, gray);
        grayToIndigoAnimator.setDuration(500);
        indigoToGrayAnimator.setDuration(500);
        grayToIndigoAnimator.addUpdateListener(_grayToIndigoAnimator_
                -> binding.btnDone.getBackground().setColorFilter((int) _grayToIndigoAnimator_.getAnimatedValue(), PorterDuff.Mode.SRC_IN));
        indigoToGrayAnimator.addUpdateListener(_indigoToGrayAnimator_
                -> binding.btnDone.getBackground().setColorFilter((int) _indigoToGrayAnimator_.getAnimatedValue(), PorterDuff.Mode.SRC_IN));

        if (copyRentalFormObservable == null) {
            if (binding.btnDone.isEnabled()) {
                indigoToGrayAnimator.start();
                binding.btnDone.setEnabled(false);
            }
        } else {
            try {
                if (!usedRentalFormObservable.customizedEquals(copyRentalFormObservable)) {
                    if (!binding.btnDone.isEnabled()) {
                        grayToIndigoAnimator.start();
                        binding.btnDone.setEnabled(true);
                    }
                } else {
                    if (binding.btnDone.isEnabled()) {
                        indigoToGrayAnimator.start();
                        binding.btnDone.setEnabled(false);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                if (binding.btnDone.isEnabled()) {
                    indigoToGrayAnimator.start();
                    binding.btnDone.setEnabled(false);
                }
            }
        }
        return true;
    });
    private AtomicBoolean stopped = new AtomicBoolean(false);
    private Thread watcher = new Thread(() -> {
        Long lastTimeSendingMessage = System.currentTimeMillis();
        while (!stopped.get()) {
            Long now = System.currentTimeMillis();
            if (now - lastTimeSendingMessage >= 500) {
                handler.sendEmptyMessage(0);
                lastTimeSendingMessage = now;
            }
        }
        Log.i("FragmentEditRentalForm Watcher", "Has Done");
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditRentalFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rentalFormViewModel = new ViewModelProvider(requireActivity()).get(RentalFormViewModel.class);
        usedRentalFormObservable = rentalFormViewModel.getObservable(requireArguments().getInt("id"));
        if (usedRentalFormObservable != null) {
            usedRentalFormObservable = usedRentalFormObservable.customizedClone();
            copyRentalFormObservable = usedRentalFormObservable.customizedClone();
            binding.setRentalFormObservable(usedRentalFormObservable);
            rentalFormViewModel.findGuest(usedRentalFormObservable);
            rentalFormViewModel.findGuest(copyRentalFormObservable);
        } else {
            usedRentalFormObservable = new RentalFormObservable();
            copyRentalFormObservable = null;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, new ArrayList<String>());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerChooseRoom.setAdapter(arrayAdapter);
        RoomViewModel roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        roomViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomObservables -> {
            arrayAdapter.clear();
            arrayAdapter.addAll(updatedRoomObservables.stream().filter(roomObservable -> !roomObservable.getIsOccupied()).map(RoomObservable::getName).toArray(String[]::new));
            arrayAdapter.add(roomViewModel.getRoomName(usedRentalFormObservable.getRoomId()));
            binding.spinnerChooseRoom.setSelection(arrayAdapter.getPosition(roomViewModel.getRoomName(usedRentalFormObservable.getRoomId())));
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
                if (!usedRentalFormObservable.getIsResolved()) {
                    if (usedRentalFormObservable.getPricePerDay() != null && usedRentalFormObservable.getRentalDays() != null) {
                        usedRentalFormObservable.setAmount(usedRentalFormObservable.getPricePerDay() * usedRentalFormObservable.getRentalDays());
                        usedRentalFormObservable.notifyPropertyChanged(BR.amountString);
                    }
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
                usedRentalFormObservable.setStartDateString(startDate.toString());
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
                if (!usedRentalFormObservable.getIsResolved()) {
                    if (usedRentalFormObservable.getPricePerDay() != null && usedRentalFormObservable.getRentalDays() != null) {
                        usedRentalFormObservable.setAmount(usedRentalFormObservable.getPricePerDay() * usedRentalFormObservable.getRentalDays());
                        usedRentalFormObservable.notifyPropertyChanged(BR.amountString);
                    }
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
                if (!usedRentalFormObservable.getIsResolved()) {
                    if (usedRentalFormObservable.getNumberOfGuestsString() == null ||
                            usedRentalFormObservable.getNumberOfGuestsString().equals("")) {
                        return;
                    }
                    rentalFormViewModel.findPrice(usedRentalFormObservable);
                }
            }
        });

        binding.spinnerChooseRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!usedRentalFormObservable.getIsResolved()) {
                    List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue();
                    if (roomObservables != null) {
                        roomObservables = roomObservables.stream().filter(roomObservable -> !roomObservable.getIsOccupied()).collect(Collectors.toList());
                        roomObservables.add(roomViewModel.getObservable(usedRentalFormObservable.getRoomId()));
                        usedRentalFormObservable.setRoomId(roomObservables.get(i).getId());
                        if (usedRentalFormObservable.getNumberOfGuestsString() == null || usedRentalFormObservable.getNumberOfGuestsString().equals("")) {
                            return;
                        }
                        rentalFormViewModel.findPrice(usedRentalFormObservable);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        watcher.start();
        binding.btnDone.setEnabled(false);
        binding.btnDone.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                if (binding.edtIdNumber.isFocused()) {
                    Toast.makeText(requireActivity(), "Finish Entering Id Number To Continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                rentalFormViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                FailureDialogFragment failureDialogFragment = new FailureDialogFragment(apolloErrors.get(0).getMessage());
                                failureDialogFragment.showNow(getParentFragmentManager(), "FragmentEditRentalForm Failure");
                            }
                        });
                    }
                };
                rentalFormViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            SuccessDialogFragment successDialogFragment = new SuccessDialogFragment("Updated successfully");
                            successDialogFragment.showNow(getParentFragmentManager(), "FragmentEditRentalForm Success");
                            NavHostFragment.findNavController(this).popBackStack();
                        });
                    }
                };
                rentalFormViewModel.onFailureCallback = null;
                if (rentalFormViewModel.checkObservable(usedRentalFormObservable, requireContext(), "billId")) {
                    rentalFormViewModel.update(usedRentalFormObservable, copyRentalFormObservable);
//                    if ((Math.abs(rentalFormObservable1.getAmount()) != Math.abs(rentalFormObservable.getAmount())) && rentalFormObservable.getBillId() != null) {
//                        Double amount;
//                        if (Math.abs(billObservable.getCost()) == Math.abs(rentalFormObservable1.getAmount()))
//                            amount = rentalFormObservable.getAmount();
//                        else
//                            amount = billObservable.getCost() + rentalFormObservable.getAmount() - rentalFormObservable1.getAmount();
//                        billViewModel.updateAmount(billObservable, amount);
//                    }
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

        if (usedRentalFormObservable.getIsResolved() != null && usedRentalFormObservable.getIsResolved()) {
            try {
                binding.radioResolved.setEnabled(false);
                binding.radioNotResolved.setEnabled(false);
                binding.spinnerChooseRoom.setEnabled(false);
                binding.spinnerChooseRoom.getBackground().setColorFilter(requireContext().getColor(R.color.gray_400), PorterDuff.Mode.MULTIPLY);
                for (Field field : binding.getClass().getFields()) {
                    field.setAccessible(true);
                    if (field.getName().startsWith("edt")) {
                        EditText editText = (EditText) field.get(binding);
                        if (editText != null) {
                            editText.setEnabled(false);
                            editText.setHintTextColor(requireContext().getColor(R.color.white_A700_cc));
                            editText.getBackground().setColorFilter(requireContext().getColor(R.color.gray_400), PorterDuff.Mode.MULTIPLY);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopped.set(true);
        while (watcher.isAlive()) {
            Log.i("FragmentEditRentalForm Watcher", "Still Alive");
        }

        stopped = null;
        watcher = null;
        handler = null;
        binding = null;
        billViewModel = null;
        billObservable = null;
        rentalFormViewModel = null;
        usedRentalFormObservable = null;
        copyRentalFormObservable = null;
    }

}
