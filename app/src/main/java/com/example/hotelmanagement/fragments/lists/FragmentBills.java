package com.example.hotelmanagement.fragments.lists;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.common.Common;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.BillAdapter;
import com.example.hotelmanagement.databinding.FragmentBillsBinding;
import com.example.hotelmanagement.dialogs.DialogFragmentFailure;
import com.example.hotelmanagement.dialogs.DialogFragmentSuccess;
import com.example.hotelmanagement.dialogs.DialogFragmentWarning;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.example.search.SearchProcessor;
import com.example.search.SearchStrategyBill;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentBills extends Fragment implements BillAdapter.BillListener {

    private BillViewModel billViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentBillsBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<BillObservable>> onSearchBillObservablesConsumer;

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

        Common.beautifySearchView(binding.billsSearchView, requireContext());

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

        Common.setupSearchFeatureInListLikeFragment(searchProcessor
                , binding.billsSearchView
                , new SearchStrategyBill(requireActivity())
                , onSearchBillObservablesConsumer, billAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        billViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
        searchProcessor = null;
        onSearchBillObservablesConsumer = null;
        Common.searchViewOnFocusChangeForwardingHandler = null;
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
        Consumer<DialogFragmentWarning.Answer> onCancelHandler = answer -> {
            if (answer == DialogFragmentWarning.Answer.YES) {
                billViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                DialogFragmentFailure.newOne(getParentFragmentManager()
                                        , "FragmentBills Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                billViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            String message = "Success: Your item has been deleted successfully.";
                            DialogFragmentSuccess.newOne(getParentFragmentManager()
                                    , "FragmentBills Success", message);
                        });
                    }
                };
                billViewModel.onFailureCallback = null;
                billViewModel.delete(billObservable);
            }
        };
        String message = "Caution: Deleting this item will result in permanent removal from the system.";
        DialogFragmentWarning.newOne(getParentFragmentManager(), "FragmentBills Warning", message, onCancelHandler);
    }

}