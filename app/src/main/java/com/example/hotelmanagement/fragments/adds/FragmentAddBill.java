package com.example.hotelmanagement.fragments.adds;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentAddBillBinding;
import com.example.hotelmanagement.dialog.watcher.DialogFragmentWatcher;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.popupwindow.implementation.PopupWindowLoading;
import com.example.hotelmanagement.viewmodel.implementation.BillViewModel;

public class FragmentAddBill extends Fragment {

    @Nullable
    private PopupWindowLoading popupWindowLoading;

    private FragmentAddBillBinding binding;
    private BillViewModel billViewModel;
    private BillObservable billObservable;

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
                billViewModel.findGuestByGuestIdNumber(billObservable);
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
                    Common.showCustomSnackBar("Finish Entering Id Number To Continue".toUpperCase(), requireContext(), binding.getRoot());
                    return;
                }
                billViewModel.findBillCostByGuestIdAndRentalFormIsResolvedFalse(billObservable);
            }
        });

        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                billViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    String failureTag = "FragmentAddBill Failure";
                    DialogFragmentWatcher.dialogFragmentFailureSubscribe(failureTag, Common.getFailureMessage(
                            apolloErrors, apolloException, cloudinaryErrorInfo));
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (popupWindowLoading != null) {
                                popupWindowLoading.dismiss();
                            }
                        });
                    }
                };
                billViewModel.onSuccessCallback = () -> {
                    String successTag = "FragmentAddBill Success";
                    DialogFragmentWatcher.dialogFragmentSuccessSubscribe(successTag, Common.getSuccessMessage(
                            Common.Action.INSERT_ITEM));
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            billObservable = new BillObservable();
                            binding.setBillObservable(billObservable);
                            billObservable.setIsPaid(false);
                            if (popupWindowLoading != null) {
                                popupWindowLoading.dismiss();
                            }
                        });
                    }
                };
                billViewModel.onFailureCallback = null;
                if (billViewModel.checkObservable(billObservable, requireContext(), binding.getRoot())) {
                    if (billObservable.getCost() == 0) {
                        Common.showCustomSnackBar("This guest does not have any rental form".toUpperCase(), requireContext(), binding.getRoot());
                    } else {
                        billViewModel.insert(billObservable);
                        popupWindowLoading = PopupWindowLoading.newOne(getLayoutInflater(), binding.linearAddBill);
                        popupWindowLoading.showAsDropDown
                                (binding.linearAddBill);
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Common.hideKeyboard(requireActivity());
        });

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        billViewModel = null;
        billObservable = null;
        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
    }

}
