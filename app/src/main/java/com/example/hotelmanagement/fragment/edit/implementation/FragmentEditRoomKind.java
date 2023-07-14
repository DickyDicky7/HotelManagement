package com.example.hotelmanagement.fragment.edit.implementation;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentEditRoomKindBinding;
import com.example.hotelmanagement.observable.implementation.RoomKindObservable;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FragmentEditRoomKind extends Fragment {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);
    @NonNull
    private final AtomicBoolean alreadyPoppedBackStackNow = new AtomicBoolean(false);

    private FragmentEditRoomKindBinding binding;
    private RoomKindViewModel roomKindViewModel;
    private RoomKindObservable usedRoomKindObservable;
    private RoomKindObservable copyRoomKindObservable;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("Local Image URI", uri.toString());
                    usedRoomKindObservable.setImageURL(uri.toString());
                    binding.edtImage.setColorFilter(Color.TRANSPARENT);
                    Glide.with(this).load(uri).centerInside().into(binding.edtImage);
                    requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            });

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

        if (copyRoomKindObservable == null) {
            if (binding.btnDone.isEnabled()) {
                indigoToGrayAnimator.start();
                binding.btnDone.setEnabled(false);
            }
        } else {
            try {
                if (!usedRoomKindObservable.customizedEquals(copyRoomKindObservable)) {
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
        Log.i("FragmentEditRoomKind Watcher", "Has Done");
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditRoomKindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        usedRoomKindObservable = roomKindViewModel.getObservable(requireArguments().getInt("id"));
        if (usedRoomKindObservable != null) {
            usedRoomKindObservable = usedRoomKindObservable.customizedClone();
            copyRoomKindObservable = usedRoomKindObservable.customizedClone();
            binding.setRoomKindObservable(usedRoomKindObservable);
        } else {
            usedRoomKindObservable = new RoomKindObservable();
            copyRoomKindObservable = null;
        }

        watcher.start();
        binding.btnDone.setEnabled(false);
        binding.btnDone.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        binding.btnDone.setOnClickListener(_view_ -> {
            Consumer<RoomKindObservable> beforeUpdatePrepareUsedExtendedObservableConsumer = null;
            String successTag = "FragmentEditRoomKind Success";
            String failureTag = "FragmentEditRoomKind Failure";
            Common.onButtonDoneFragmentEdtClickHandler(
                    beforeUpdatePrepareUsedExtendedObservableConsumer,
                    roomKindViewModel,
                    usedRoomKindObservable,
                    copyRoomKindObservable,
                    successTag,
                    failureTag,
                    binding.linearEditRoomKind,
                    NavHostFragment.findNavController(this),
                    requireActivity(),
                    dismissPopupWindowLoading,
                    alreadyPoppedBackStackNow
            );
        });

        binding.edtImage.setColorFilter(Color.WHITE);
        binding.edtImage.setOnClickListener(_view_ -> {
            Common.hideKeyboard(requireActivity());
            pickVisualMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        binding.edtImage.setColorFilter(Color.TRANSPARENT);
        Glide.with(this).load(usedRoomKindObservable.getImageURL()).centerInside().into(binding.edtImage);

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopped.set(true);
        while (watcher.isAlive()) {
            Log.i("FragmentEditRoomKind Watcher", "Still Alive");
        }

        stopped = null;
        watcher = null;
        handler = null;
        binding = null;
        roomKindViewModel = null;
        usedRoomKindObservable = null;
        copyRoomKindObservable = null;
        pickVisualMediaLauncher.unregister();
        pickVisualMediaLauncher = null;
        dismissPopupWindowLoading.set(true);
        alreadyPoppedBackStackNow.set(true);
    }

}
