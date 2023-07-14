package com.example.hotelmanagement.fragment.edit.implementation;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.hotelmanagement.databinding.FragmentEditRoomBinding;
import com.example.hotelmanagement.observable.implementation.RoomKindObservable;
import com.example.hotelmanagement.observable.implementation.RoomObservable;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RoomViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FragmentEditRoom extends Fragment {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);
    @NonNull
    private final AtomicBoolean alreadyPoppedBackStackNow = new AtomicBoolean(false);

    private FragmentEditRoomBinding binding;
    private RoomViewModel roomViewModel;
    private RoomObservable usedRoomObservable;
    private RoomObservable copyRoomObservable;

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
            usedRoomObservable = usedRoomObservable.customizedClone();
            copyRoomObservable = usedRoomObservable.customizedClone();
            binding.setRoomObservable(usedRoomObservable);
        } else {
            usedRoomObservable = new RoomObservable();
            copyRoomObservable = null;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapter.setDropDownViewResource(R.layout.item_spinner);
        binding.spinnerChooseRoomKind.setAdapter(arrayAdapter);

        RoomKindViewModel roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomKindObservables -> {
            arrayAdapter.clear();
            arrayAdapter.addAll(updatedRoomKindObservables.stream().map(RoomKindObservable::getName).toArray(String[]::new));
            binding.spinnerChooseRoomKind.setSelection(arrayAdapter.getPosition
                    (roomKindViewModel.getRoomKindName(usedRoomObservable.getRoomKindId())), true);
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
            Consumer<RoomObservable> beforeUpdatePrepareUsedExtendedObservableConsumer = _usedRoomObservable_ -> {
                if (_usedRoomObservable_.getNote() != null
                        && _usedRoomObservable_.getNote().equals("")) {
                    _usedRoomObservable_.setNote(null);
                }
                if (_usedRoomObservable_.getDescription() != null
                        && _usedRoomObservable_.getDescription().equals("")) {
                    _usedRoomObservable_.setDescription(null);
                }
            };
            String successTag = "FragmentEditRoom Success";
            String failureTag = "FragmentEditRoom Failure";
            Common.onButtonDoneFragmentEdtClickHandler(
                    beforeUpdatePrepareUsedExtendedObservableConsumer,
                    roomViewModel,
                    usedRoomObservable,
                    copyRoomObservable,
                    successTag,
                    failureTag,
                    binding.linearEditRoom,
                    NavHostFragment.findNavController(this),
                    requireActivity(),
                    dismissPopupWindowLoading,
                    alreadyPoppedBackStackNow,
                    "note", "description"
            );
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
        dismissPopupWindowLoading.set(true);
        alreadyPoppedBackStackNow.set(true);
    }

}
