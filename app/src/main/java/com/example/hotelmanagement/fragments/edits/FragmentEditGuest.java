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
import com.example.hotelmanagement.databinding.FragmentEditGuestBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;
import com.example.hotelmanagement.dialog.SuccessDialogFragment;
import com.example.hotelmanagement.observables.GuestKindObservable;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewmodels.GuestKindViewModel;
import com.example.hotelmanagement.viewmodels.GuestViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FragmentEditGuest extends Fragment {

    private FragmentEditGuestBinding binding;
    private GuestViewModel guestViewModel;
    private GuestObservable usedGuestObservable;
    private GuestObservable copyGuestObservable;

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

        if (copyGuestObservable == null) {
            if (binding.btnDone.isEnabled()) {
                indigoToGrayAnimator.start();
                binding.btnDone.setEnabled(false);
            }
        } else {
            try {
                if (!usedGuestObservable.customizedEquals(copyGuestObservable)) {
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
        Log.i("FragmentEditGuest Watcher", "Has Done");
    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditGuestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        guestViewModel = new ViewModelProvider(requireActivity()).get(GuestViewModel.class);
        usedGuestObservable = guestViewModel.getObservable(requireArguments().getInt("id"));
        if (usedGuestObservable != null) {
            usedGuestObservable = usedGuestObservable.customizedClone();
            copyGuestObservable = usedGuestObservable.customizedClone();
            binding.setGuestObservable(usedGuestObservable);
        } else {
            usedGuestObservable = new GuestObservable();
            copyGuestObservable = null;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, new ArrayList<String>());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        binding.spinnerChooseGuestKind.setAdapter(arrayAdapter);

        GuestKindViewModel guestKindViewModel = new ViewModelProvider(requireActivity()).get(GuestKindViewModel.class);
        guestKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedGuestKindObservables -> {
            arrayAdapter.addAll(updatedGuestKindObservables.stream().map(GuestKindObservable::getName).toArray(String[]::new));
            binding.spinnerChooseGuestKind.setSelection(arrayAdapter.getPosition(guestKindViewModel.getGuestKindName(usedGuestObservable.getGuestKindId())));
        });

        binding.spinnerChooseGuestKind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<GuestKindObservable> guestKindObservables = guestKindViewModel.getModelState().getValue();
                if (guestKindObservables != null) {
                    usedGuestObservable.setGuestKindId(guestKindObservables.get(i).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                usedGuestObservable.setGuestKindId(null);
            }
        });

        watcher.start();
        binding.btnDone.setEnabled(false);
        binding.btnDone.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                guestViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                FailureDialogFragment.newOne(getParentFragmentManager()
                                        , "FragmentEditGuest Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                guestViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            String message = "Success: Your item has been updated successfully.";
                            SuccessDialogFragment.newOne(getParentFragmentManager()
                                    , "FragmentEditGuest Success", message);
                            NavHostFragment.findNavController(this).popBackStack();
                        });
                    }
                };
                guestViewModel.onFailureCallback = null;
                if (guestViewModel.checkObservable(usedGuestObservable, requireContext())) {
                    guestViewModel.update(usedGuestObservable, copyGuestObservable);
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
            Log.i("FragmentEditGuest Watcher", "Still Alive");
        }

        stopped = null;
        watcher = null;
        handler = null;
        binding = null;
        guestViewModel = null;
        usedGuestObservable = null;
        copyGuestObservable = null;
    }

}
