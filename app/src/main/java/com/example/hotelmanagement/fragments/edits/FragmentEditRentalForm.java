package com.example.hotelmanagement.fragments.edits;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.common.Common;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentEditRentalFormBinding;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FragmentEditRentalForm extends Fragment {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);
    @NonNull
    private final AtomicBoolean alreadyPoppedBackStackNow = new AtomicBoolean(false);

    private FragmentEditRentalFormBinding binding;
    private RentalFormViewModel rentalFormViewModel;
    private RentalFormObservable usedRentalFormObservable;
    private RentalFormObservable copyRentalFormObservable;

    private Handler handler = new Handler(message -> {

        if (getContext() == null) {
            return false;
        }

        int gray = Color.GRAY;
        int indigo = requireContext().getColor(R.color.indigo_100);
        ValueAnimator grayToIndigoAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), gray, indigo);
        ValueAnimator indigoToGrayAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), indigo, gray);
        grayToIndigoAnimator.setDuration(500);
        indigoToGrayAnimator.setDuration(500);
        grayToIndigoAnimator.addUpdateListener(_grayToIndigoAnimator_ -> {
            if (getContext() != null) {
                binding.btnDone.getBackground().setColorFilter((int) _grayToIndigoAnimator_.getAnimatedValue(), PorterDuff.Mode.SRC_IN);
            }
        });
        indigoToGrayAnimator.addUpdateListener(_indigoToGrayAnimator_ -> {
            if (getContext() != null) {
                binding.btnDone.getBackground().setColorFilter((int) _indigoToGrayAnimator_.getAnimatedValue(), PorterDuff.Mode.SRC_IN);
            }
        });

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rentalFormViewModel = new ViewModelProvider(requireActivity()).get(RentalFormViewModel.class);
        usedRentalFormObservable = rentalFormViewModel.getObservable(requireArguments().getInt("id"));
        if (usedRentalFormObservable != null) {
            usedRentalFormObservable = usedRentalFormObservable.customizedClone();
            copyRentalFormObservable = usedRentalFormObservable.customizedClone();
            binding.setRentalFormObservable(usedRentalFormObservable);
            rentalFormViewModel.findGuestByGuestId(usedRentalFormObservable);
            rentalFormViewModel.findGuestByGuestId(copyRentalFormObservable);
        } else {
            usedRentalFormObservable = new RentalFormObservable();
            copyRentalFormObservable = null;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        binding.spinnerChooseRoom.setAdapter(arrayAdapter);

        RoomViewModel roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        roomViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomObservables -> {
            arrayAdapter.clear();
            arrayAdapter.addAll(updatedRoomObservables.stream().filter(roomObservable -> !roomObservable.getIsOccupied()
            ).map(RoomObservable::getName).toArray(String[]::new));
            arrayAdapter.add(roomViewModel.getRoomName(usedRentalFormObservable.getRoomId()));
            binding.spinnerChooseRoom.setSelection(arrayAdapter.getPosition(
                    roomViewModel.getRoomName(usedRentalFormObservable.getRoomId())), true);
        });

        binding.edtIdNumber.setOnFocusChangeListener((_view_, b) -> {
            if (!b) {
                if (!copyRentalFormObservable.getIsResolved()) {
                    rentalFormViewModel.findGuestByGuestIdNumber(usedRentalFormObservable);
                }
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
                if (!copyRentalFormObservable.getIsResolved()) {
                    if (usedRentalFormObservable.getPricePerDay() != null && usedRentalFormObservable.getRentalDays() != null) {
                        usedRentalFormObservable.setAmount(usedRentalFormObservable.getPricePerDay() * usedRentalFormObservable.getRentalDays());
                        usedRentalFormObservable.notifyPropertyChanged(BR.amountString);
                    }
                }
            }
        });

        binding.edtStartDate.setOnClickListener(_view_ -> {
            Calendar currentDate = Calendar.getInstance();
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH);
            int dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog startDatePicker = new DatePickerDialog(requireActivity(), (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                LocalDate startDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay);
                usedRentalFormObservable.setStartDateString(startDate.toString());
            }, year, month, dayOfMonth);
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
                if (!copyRentalFormObservable.getIsResolved()) {
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
                if (!copyRentalFormObservable.getIsResolved()) {
                    if (usedRentalFormObservable.getNumberOfGuestsString() == null || usedRentalFormObservable.getNumberOfGuestsString().equals("")) {
                        return;
                    }
                    rentalFormViewModel.findRentalFormPricePerDayByRoomId(usedRentalFormObservable);
                }
            }
        });

        binding.spinnerChooseRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!copyRentalFormObservable.getIsResolved()) {
                    List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue();
                    if (roomObservables != null) {
                        roomObservables = roomObservables.stream().filter(roomObservable -> !roomObservable.getIsOccupied()).collect(Collectors.toList());
                        roomObservables.add(roomViewModel.getObservable(usedRentalFormObservable.getRoomId()));
                        usedRentalFormObservable.setRoomId(roomObservables.get(i).getId());
                        if (usedRentalFormObservable.getNumberOfGuestsString() == null || usedRentalFormObservable.getNumberOfGuestsString().equals("")) {
                            return;
                        }
                        rentalFormViewModel.findRentalFormPricePerDayByRoomId(usedRentalFormObservable);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                if (!copyRentalFormObservable.getIsResolved()) {
                    usedRentalFormObservable.setRoomId(null);
                    usedRentalFormObservable.setPricePerDay(null);
                    usedRentalFormObservable.notifyPropertyChanged(BR.pricePerDayString);
                }
            }
        });

        watcher.start();
        binding.btnDone.setEnabled(false);
        binding.btnDone.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        binding.btnDone.setOnClickListener(_view_ -> {
            if (binding.edtIdNumber.isFocused()) {
                Common.showCustomSnackBar("Finish Entering Id Number To Continue".toUpperCase(), requireContext(), binding.getRoot());
                return;
            }
            Consumer<RentalFormObservable> beforeUpdatePrepareUsedExtendedObservableConsumer = null;
            String successTag = "FragmentEditRentalForm Success";
            String failureTag = "FragmentEditRentalForm Failure";
            Common.onButtonDoneFragmentEdtClickHandler(
                    beforeUpdatePrepareUsedExtendedObservableConsumer,
                    rentalFormViewModel,
                    usedRentalFormObservable,
                    copyRentalFormObservable,
                    successTag,
                    failureTag,
                    binding.linearEditRentalForm,
                    NavHostFragment.findNavController(this),
                    requireActivity(),
                    dismissPopupWindowLoading,
                    alreadyPoppedBackStackNow,
                    "billId"
            );
        });

        if (copyRentalFormObservable.getIsResolved() != null && copyRentalFormObservable.getIsResolved()) {
            try {
                binding.radioResolved.setEnabled(false);
                binding.radioNotResolved.setEnabled(false);
                binding.spinnerChooseRoom.setEnabled(false);
                binding.spinnerChooseRoom.getBackground().setColorFilter(requireContext().getColor(R.color.gray_100), PorterDuff.Mode.MULTIPLY);
                for (Field field : binding.getClass().getFields()) {
                    field.setAccessible(true);
                    if (field.getName().startsWith("edt")) {
                        EditText editText = (EditText) field.get(binding);
                        if (editText != null) {
                            editText.setEnabled(false);
                            editText.setHintTextColor(requireContext().getColor(R.color.white_600));
                            editText.getBackground().setColorFilter(requireContext().getColor(R.color.gray_100), PorterDuff.Mode.MULTIPLY);
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
        rentalFormViewModel = null;
        usedRentalFormObservable = null;
        copyRentalFormObservable = null;
        dismissPopupWindowLoading.set(true);
        alreadyPoppedBackStackNow.set(true);
    }

}
