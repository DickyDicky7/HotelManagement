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

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.implementation.GuestAdapter;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentListGuestBinding;
import com.example.hotelmanagement.observable.implementation.GuestObservable;
import com.example.hotelmanagement.search.processor.SearchProcessor;
import com.example.hotelmanagement.search.strategy.implementation.SearchStrategyGuest;
import com.example.hotelmanagement.viewmodel.implementation.GuestViewModel;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentListGuest extends Fragment implements GuestAdapter.GuestListener {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);

    private GuestViewModel guestViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentListGuestBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<GuestObservable>> onSearchGuestObservablesConsumer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListGuestBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Common.beautifySearchView(binding.guestsSearchView, requireContext());

        binding.guestsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListGuest_to_fragmentAddGuest);
        });

        binding.guestsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        GuestAdapter guestAdapter = new GuestAdapter(requireActivity(), this);
        binding.guestsRecyclerView.setAdapter(new ScaleInAnimationAdapter(guestAdapter));
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireContext());
        flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        binding.guestsRecyclerView.setLayoutManager(flexboxLayoutManager);
        guestViewModel = new ViewModelProvider(requireActivity()).get(GuestViewModel.class);
        guestViewModel.getModelState().observe(getViewLifecycleOwner(), updatedGuestObservables -> {
            guestAdapter.Clear();
            guestAdapter.Fill(updatedGuestObservables);
        });

        binding.guestsBtnAdd.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> {
            if (binding != null && binding.guestsBtnAdd.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.SlideOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.guestsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.guestsBtnAdd);
            }
        };
        binding.guestsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.guestsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.SlideInUp).duration(500).onStart(animator -> binding.guestsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.guestsBtnAdd);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

        binding.guestsBtnBack.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        Common.setupSearchFeatureInListLikeFragment(searchProcessor
                , binding.guestsSearchView
                , new SearchStrategyGuest(requireActivity())
                , onSearchGuestObservablesConsumer, guestAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        dismissPopupWindowLoading.set(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        guestViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
        searchProcessor = null;
        dismissPopupWindowLoading.set(true);
        onSearchGuestObservablesConsumer = null;
        Common.searchViewOnFocusChangeForwardingHandler = null;
    }

    @Override
    public void onJustGuestClick(GuestObservable guestObservable) {

    }

    @Override
    public void onEditGuestClick(GuestObservable guestObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", guestObservable.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListGuest_to_fragmentEditGuest, bundle);
    }

    @Override
    public void onDeleGuestClick(GuestObservable guestObservable) {
        String warningTag = "FragmentListGuest Warning";
        String successTag = "FragmentListGuest Success";
        String failureTag = "FragmentListGuest Failure";
        Common.onDeleteRecyclerViewItemClickHandler(guestViewModel, guestObservable
                , warningTag
                , successTag
                , failureTag
                , binding.getRoot()
                , requireActivity()
                , dismissPopupWindowLoading);
    }

}