package com.example.hotelmanagement.fragments.edits;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentEditRoomBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentEditRoom extends Fragment {

    private FragmentEditRoomBinding binding;
    private RoomViewModel roomViewModel;
    private RoomObservable usedRoomObservable;
    private RoomObservable copyRoomObservable;

    private Handler handler = new Handler(message -> {
        if (getContext() == null) {
            System.out.println("Error");
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

        if (copyRoomObservable == null) {
            if (binding.btnDone.isEnabled()) {
                indigoToGrayAnimator.start();
                binding.btnDone.setEnabled(false);
            }
        } else {
            try {
                if (!usedRoomObservable.customizedEquals(copyRoomObservable)) {
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
        Log.i("FragmentEditRoom Watcher", "Has Done");
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        usedRoomObservable = roomViewModel.getObservable(requireArguments().getInt("id"));
        if (usedRoomObservable != null) {
            copyRoomObservable = usedRoomObservable.customizedClone();
            binding.setRoomObservable(usedRoomObservable);
        } else {
            copyRoomObservable = null;
            copyRoomObservable = new RoomObservable();
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, new ArrayList<String>());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerChooseRoomKind.setAdapter(arrayAdapter);

        RoomKindViewModel roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomKindObservables -> {
            arrayAdapter.addAll(updatedRoomKindObservables.stream().map(RoomKindObservable::getName).toArray(String[]::new));
            binding.spinnerChooseRoomKind.setSelection(arrayAdapter.getPosition(roomKindViewModel.getRoomKindName(usedRoomObservable.getRoomKindId())));
        });

        binding.spinnerChooseRoomKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState().getValue();
                if (roomKindObservables != null) {
                    usedRoomObservable.setRoomKindId(roomKindObservables.get(i).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                usedRoomObservable.setRoomKindId(null);
            }
        });

        watcher.start();
        binding.btnDone.setEnabled(false);
        binding.btnDone.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                roomViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> NavHostFragment.findNavController(this).popBackStack());
                    }
                };
                roomViewModel.onFailureCallback = null;
                if (roomViewModel.checkObservable(usedRoomObservable, requireContext())) {
                    if (usedRoomObservable.getNote() != null && usedRoomObservable.getNote().equals("")) {
                        usedRoomObservable.setNote(null);
                    }
                    if (usedRoomObservable.getDescription() != null && usedRoomObservable.getDescription().equals("")) {
                        usedRoomObservable.setDescription(null);
                    }
                    roomViewModel.update(usedRoomObservable, copyRoomObservable);
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

        stopped.set(true);
        while (watcher.isAlive()) {
            Log.i("FragmentEditRoom Watcher", "Still Alive");
        }

        stopped = null;
        watcher = null;
        handler = null;
        binding = null;
        roomViewModel = null;
        usedRoomObservable = null;
        copyRoomObservable = null;
    }

}
