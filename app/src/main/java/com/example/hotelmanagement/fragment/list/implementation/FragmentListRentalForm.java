package com.example.hotelmanagement.fragment.list.implementation;

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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.implementation.RentalFormAdapter;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentListRentalFormBinding;
import com.example.hotelmanagement.observable.implementation.RentalFormObservable;
import com.example.hotelmanagement.search.processor.SearchProcessor;
import com.example.hotelmanagement.search.strategy.implementation.SearchStrategyRentalForm;
import com.example.hotelmanagement.viewmodel.implementation.RentalFormViewModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentListRentalForm extends Fragment implements RentalFormAdapter.RentalFormListener {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);

    private RentalFormViewModel rentalFormViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentListRentalFormBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<RentalFormObservable>> onSearchRentalFormObservablesConsumer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListRentalFormBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Common.beautifySearchView(binding.rentalFormsSearchView, requireContext());

        binding.rentalFormsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListRentalForm_to_fragmentAddRentalForm);
        });

        binding.rentalFormsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        RentalFormAdapter rentalFormAdapter = new RentalFormAdapter(requireActivity(), this);
        binding.rentalFormsRecyclerView.setAdapter(new ScaleInAnimationAdapter(rentalFormAdapter));
        binding.rentalFormsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        rentalFormViewModel = new ViewModelProvider(requireActivity()).get(RentalFormViewModel.class);
        rentalFormViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRentalFormObservables -> {
            rentalFormAdapter.Clear();
            rentalFormAdapter.Fill(updatedRentalFormObservables);
        });

        binding.rentalFormsBtnAdd.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> {
            if (binding != null && binding.rentalFormsBtnAdd.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.SlideOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.rentalFormsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.rentalFormsBtnAdd);
            }
        };
        binding.rentalFormsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.rentalFormsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.SlideInUp).duration(500).onStart(animator -> binding.rentalFormsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.rentalFormsBtnAdd);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

        binding.rentalFormsBtnBack.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        Common.setupSearchFeatureInListLikeFragment(searchProcessor
                , binding.rentalFormsSearchView
                , new SearchStrategyRentalForm(requireActivity())
                , onSearchRentalFormObservablesConsumer, rentalFormAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        dismissPopupWindowLoading.set(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rentalFormViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
        searchProcessor = null;
        dismissPopupWindowLoading.set(true);
        onSearchRentalFormObservablesConsumer = null;
        Common.searchViewOnFocusChangeForwardingHandler = null;
    }

    @Override
    public void onJustRentalFormClick(RentalFormObservable rentalFormObservable) {

    }

    @Override
    public void onEditRentalFormClick(RentalFormObservable rentalFormObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", rentalFormObservable.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListRentalForm_to_fragmentEditRentalForm, bundle);
    }

    @Override
    public void onDeleRentalFormClick(RentalFormObservable rentalFormObservable) {
        String warningTag = "FragmentListRentalForm Warning";
        String successTag = "FragmentListRentalForm Success";
        String failureTag = "FragmentListRentalForm Failure";
        Common.onDeleteRecyclerViewItemClickHandler(rentalFormViewModel, rentalFormObservable
                , warningTag
                , successTag
                , failureTag
                , binding.getRoot()
                , requireActivity()
                , dismissPopupWindowLoading);
    }

}