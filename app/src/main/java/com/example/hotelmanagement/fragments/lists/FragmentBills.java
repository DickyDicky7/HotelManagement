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
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.implementation.BillAdapter;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentBillsBinding;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.search.processor.SearchProcessor;
import com.example.hotelmanagement.search.strategy.implementation.SearchStrategyBill;
import com.example.hotelmanagement.viewmodel.implementation.BillViewModel;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentBills extends Fragment implements BillAdapter.BillListener {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);

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
                YoYo.with(Techniques.SlideOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.billsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.billsBtnAdd);
            }
        };
        binding.billsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.billsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.SlideInUp).duration(500).onStart(animator -> binding.billsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.billsBtnAdd);
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
        dismissPopupWindowLoading.set(true);
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
        String warningTag = "FragmentBills Warning";
        String successTag = "FragmentBills Success";
        String failureTag = "FragmentBills Failure";
        Common.onDeleteRecyclerViewItemClickHandler(billViewModel, billObservable
                , warningTag
                , successTag
                , failureTag
                , binding.getRoot()
                , requireActivity()
                , dismissPopupWindowLoading);
    }

}