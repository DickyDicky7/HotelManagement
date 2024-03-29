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
import com.example.hotelmanagement.adapter.implementation.RoomKindAdapter;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentListRoomKindBinding;
import com.example.hotelmanagement.observable.implementation.RoomKindObservable;
import com.example.hotelmanagement.search.processor.SearchProcessor;
import com.example.hotelmanagement.search.strategy.implementation.SearchStrategyRoomKind;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentListRoomKind extends Fragment implements RoomKindAdapter.RoomKindListener {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);

    private RoomKindViewModel roomKindViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentListRoomKindBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<RoomKindObservable>> onSearchRoomKindObservablesConsumer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListRoomKindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Common.beautifySearchView(binding.roomKindsSearchView, requireContext());

        binding.roomKindsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListRoomKind_to_fragmentAddRoomKind);
        });

        binding.roomKindsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        RoomKindAdapter roomKindAdapter = new RoomKindAdapter(requireActivity(), this);
        binding.roomKindsRecyclerView.setAdapter(new ScaleInAnimationAdapter(roomKindAdapter));
        binding.roomKindsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomKindObservables -> {
            roomKindAdapter.Clear();
            roomKindAdapter.Fill(updatedRoomKindObservables);
        });

        binding.roomKindsBtnAdd.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> {
            if (binding != null && binding.roomKindsBtnAdd.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.SlideOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomKindsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomKindsBtnAdd);
            }
        };
        binding.roomKindsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.roomKindsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.SlideInUp).duration(500).onStart(animator -> binding.roomKindsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.roomKindsBtnAdd);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

        binding.roomKindsBtnBack.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        Common.setupSearchFeatureInListLikeFragment(searchProcessor
                , binding.roomKindsSearchView
                , new SearchStrategyRoomKind(requireActivity())
                , onSearchRoomKindObservablesConsumer, roomKindAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        dismissPopupWindowLoading.set(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        roomKindViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
        searchProcessor = null;
        dismissPopupWindowLoading.set(true);
        onSearchRoomKindObservablesConsumer = null;
        Common.searchViewOnFocusChangeForwardingHandler = null;
    }

    @Override
    public void onJustRoomKindClick(RoomKindObservable roomKindObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", roomKindObservable.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListRoomKind_to_fragmentDetailRoomKind, bundle);
    }

    @Override
    public void onEditRoomKindClick(RoomKindObservable roomKindObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", roomKindObservable.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListRoomKind_to_fragmentEditRoomKind, bundle);
    }

    @Override
    public void onDeleRoomKindClick(RoomKindObservable roomKindObservable) {
        String warningTag = "FragmentListRoomKind Warning";
        String successTag = "FragmentListRoomKind Success";
        String failureTag = "FragmentListRoomKind Failure";
        Common.onDeleteRecyclerViewItemClickHandler(roomKindViewModel, roomKindObservable
                , warningTag
                , successTag
                , failureTag
                , binding.getRoot()
                , requireActivity()
                , dismissPopupWindowLoading);
    }

}