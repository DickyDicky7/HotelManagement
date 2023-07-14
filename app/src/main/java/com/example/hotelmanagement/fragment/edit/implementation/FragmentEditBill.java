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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentEditBillBinding;
import com.example.hotelmanagement.observable.implementation.BillObservable;
import com.example.hotelmanagement.viewmodel.implementation.BillViewModel;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FragmentEditBill extends Fragment {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);
    @NonNull
    private final AtomicBoolean alreadyPoppedBackStackNow = new AtomicBoolean(false);

    private FragmentEditBillBinding binding;
    private BillViewModel billViewModel;
    private BillObservable usedBillObservable;
    private BillObservable copyBillObservable;

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

        if (copyBillObservable == null) {
            if (binding.btnDone.isEnabled()) {
                indigoToGrayAnimator.start();
                binding.btnDone.setEnabled(false);
            }
        } else {
            try {
                if (!usedBillObservable.customizedEquals(copyBillObservable)) {
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
        Log.i("FragmentEditBill Watcher", "Has Done");
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
        usedBillObservable = billViewModel.getObservable(requireArguments().getInt("id"));
        if (usedBillObservable != null) {
            usedBillObservable = usedBillObservable.customizedClone();
            copyBillObservable = usedBillObservable.customizedClone();
            binding.setBillObservable(usedBillObservable);
            billViewModel.findGuestByGuestId(usedBillObservable);
            billViewModel.findGuestByGuestId(copyBillObservable);
        } else {
            usedBillObservable = new BillObservable();
            copyBillObservable = null;
        }

        watcher.start();
        binding.btnDone.setEnabled(false);
        binding.btnDone.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        binding.btnDone.setOnClickListener(_view_ -> {
            Consumer<BillObservable> beforeUpdatePrepareUsedExtendedObservableConsumer = null;
            String successTag = "FragmentEditBill Success";
            String failureTag = "FragmentEditBill Failure";
            Common.onButtonDoneFragmentEdtClickHandler(
                    beforeUpdatePrepareUsedExtendedObservableConsumer,
                    billViewModel,
                    usedBillObservable,
                    copyBillObservable,
                    successTag,
                    failureTag,
                    binding.linearEditBill,
                    NavHostFragment.findNavController(this),
                    requireActivity(),
                    dismissPopupWindowLoading,
                    alreadyPoppedBackStackNow
            );
        });

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        stopped.set(true);
        while (watcher.isAlive()) {
            Log.i("FragmentEditBill Watcher", "Still Alive");
        }

        stopped = null;
        watcher = null;
        handler = null;
        binding = null;
        billViewModel = null;
        usedBillObservable = null;
        copyBillObservable = null;
        dismissPopupWindowLoading.set(true);
        alreadyPoppedBackStackNow.set(true);
    }

}
