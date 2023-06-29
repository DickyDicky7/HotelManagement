package com.example.hotelmanagement.fragments.lists;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.BillAdapter;
import com.example.hotelmanagement.databinding.FragmentBillsBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;
import com.example.hotelmanagement.dialog.SuccessDialogFragment;
import com.example.hotelmanagement.dialog.WarningDialogFragment;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentBills extends Fragment implements BillAdapter.BillListener {

    private BillViewModel billViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentBillsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBillsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.billsSearchView.setIconifiedByDefault(false);
        EditText editText = binding.billsSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = binding.billsSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView closeButton = binding.billsSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        editText.setCursorVisible(true);
        editText.setTextColor(Color.GRAY);
        editText.setHintTextColor(Color.GRAY);
        searchIcon.setColorFilter(Color.GRAY);
        closeButton.setColorFilter(Color.GRAY);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editText.setOnFocusChangeListener((_view_, isFocused) -> {
            if (isFocused) {
                editText.setTextColor(requireContext().getColor(R.color.indigo_400));
                editText.setHintTextColor(requireContext().getColor(R.color.indigo_400));
                searchIcon.setColorFilter(requireContext().getColor(R.color.indigo_400));
                closeButton.setColorFilter(requireContext().getColor(R.color.indigo_400));
            } else {
                editText.setTextColor(Color.GRAY);
                editText.setHintTextColor(Color.GRAY);
                searchIcon.setColorFilter(Color.GRAY);
                closeButton.setColorFilter(Color.GRAY);
            }
        });

        binding.billsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentBills_to_fragmentAddBill);
        });

        binding.billsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        BillAdapter billAdapter = new BillAdapter(requireActivity(), this);
        binding.billsRecyclerView.setAdapter(new ScaleInAnimationAdapter(billAdapter));
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireContext());
        flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        binding.billsRecyclerView.setLayoutManager(flexboxLayoutManager);
        billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
        billViewModel.getModelState().observe(getViewLifecycleOwner(), updatedBillObservables -> {
            billAdapter.Clear();
            billAdapter.Fill(updatedBillObservables);
        });

        binding.billsBtnAdd.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> {
            if (binding != null && binding.billsBtnAdd.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.billsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.billsBtnAdd);
            }
        };
        binding.billsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.billsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.billsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.billsBtnAdd);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

        binding.billsBtnBack.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        billViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
    }

    @Override
    public void onJustBillClick(BillObservable billObservable) {

    }

    @Override
    public void onEditBillClick(BillObservable billObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", billObservable.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentBills_to_fragmentEditBill, bundle);
    }

    @Override
    public void onDeleBillClick(BillObservable billObservable) {
        Consumer<WarningDialogFragment.Answer> onCancelHandler = answer -> {
            if (answer == WarningDialogFragment.Answer.YES) {
                billViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                FailureDialogFragment.newOne(getParentFragmentManager()
                                        , "FragmentBills Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                billViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            String message = "Success: Your item has been deleted successfully.";
                            SuccessDialogFragment.newOne(getParentFragmentManager()
                                    , "FragmentBills Success", message);
                        });
                    }
                };
                billViewModel.onFailureCallback = null;
                billViewModel.delete(billObservable);
            }
        };
        String message = "Caution: Deleting this item will result in permanent removal from the system.";
        WarningDialogFragment.newOne(getParentFragmentManager(), "FragmentBills Warning", message, onCancelHandler);
    }

}