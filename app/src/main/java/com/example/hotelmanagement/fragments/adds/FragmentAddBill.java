package com.example.hotelmanagement.fragments.adds;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.databinding.FragmentAddBillBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;

public class FragmentAddBill extends Fragment {

    private FragmentAddBillBinding binding;
    private BillObservable billObservable;
    private BillViewModel billViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddBillBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        billObservable = new BillObservable();
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
        binding.setBillObservable(billObservable);

        binding.edtIdNumber.setOnFocusChangeListener((_view_, b) -> {
            if (!b) {
                billViewModel.findGuestId(billObservable);
            }
        });

        binding.edtGuestName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.edtIdNumber.isFocused()) {
                    Toast.makeText(requireContext(), "Finish Entering Id Number To Continue", Toast.LENGTH_SHORT).show();
                    return;
                }
                billViewModel.calculateCost(billObservable);
            }
        });

        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                billViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                FailureDialogFragment failureDialogFragment = new FailureDialogFragment(apolloErrors.get(0).getMessage());
                                failureDialogFragment.showNow(getParentFragmentManager(), "FragmentAddBill Failure");
                            }
                        });
                    }
                };
                billViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            billObservable = new BillObservable();
                            binding.setBillObservable(billObservable);
                            billObservable.setIsPaid(false);
                        });
                    }
                };
                billViewModel.onFailureCallback = null;
                if (billViewModel.checkObservable(billObservable, requireContext())) {
                    if (billObservable.getCost() == 0) {
                        Toast.makeText(requireActivity(), "This guest does not have any rental form", Toast.LENGTH_SHORT).show();
                    } else {
                        billViewModel.insert(billObservable);
                    }
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
        billViewModel = null;
        billObservable = null;
    }

}
