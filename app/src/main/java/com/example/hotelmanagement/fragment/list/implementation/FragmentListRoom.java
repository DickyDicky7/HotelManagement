package com.example.hotelmanagement.fragment.list.implementation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.implementation.RoomAdapter;
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentListRoomBinding;
import com.example.hotelmanagement.observable.implementation.RoomObservable;
import com.example.hotelmanagement.popupwindow.common.implementation.PopupWindowFilterRoom;
import com.example.hotelmanagement.search.processor.SearchProcessor;
import com.example.hotelmanagement.search.strategy.implementation.SearchStrategyRoom;
import com.example.hotelmanagement.viewmodel.implementation.RoomViewModel;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentListRoom extends Fragment implements RoomAdapter.RoomListener {

    @NonNull
    private final AtomicBoolean dismissPopupWindowLoading = new AtomicBoolean(false);

    private static Integer id;
    private Boolean isFstTime;

    private RoomViewModel roomViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentListRoomBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<RoomObservable>> onSearchRoomObservablesConsumer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListRoomBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isFstTime = true;
        if (id == null) {
            id = -1;
        }

        Common.beautifySearchView(binding.roomsSearchView, requireContext());

        binding.roomsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListRoom_to_fragmentAddRoom);
        });

        binding.roomsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        RoomAdapter roomAdapter = new RoomAdapter(requireActivity(), this);
        binding.roomsRecyclerView.setAdapter(new ScaleInAnimationAdapter(roomAdapter));
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireContext());
        flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        binding.roomsRecyclerView.setLayoutManager(flexboxLayoutManager);
        roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
        roomViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRoomObservables -> {
            roomAdapter.Clear();
            roomAdapter.Fill(updatedRoomObservables);
        });

        binding.roomsBtnAdd.setVisibility(View.INVISIBLE);
        binding.roomsBtnEdit.setVisibility(View.INVISIBLE);
        binding.roomsBtnDelete.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> {
            if (binding != null && binding.roomsBtnAdd.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.SlideOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomsBtnAdd);
            }
            if (binding != null && binding.roomsBtnEdit.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.SlideOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomsBtnEdit.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomsBtnEdit);
            }
            if (binding != null && binding.roomsBtnDelete.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.SlideOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomsBtnDelete.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomsBtnDelete);
            }
        };
        binding.roomsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.roomsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.SlideInUp).duration(500).onStart(animator -> binding.roomsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.roomsBtnAdd);
            }
            if (binding.roomsBtnEdit.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.SlideInUp).duration(500).onStart(animator -> binding.roomsBtnEdit.setVisibility(View.VISIBLE)).playOn(binding.roomsBtnEdit);
            }
            if (binding.roomsBtnDelete.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.SlideInUp).duration(500).onStart(animator -> binding.roomsBtnDelete.setVisibility(View.VISIBLE)).playOn(binding.roomsBtnDelete);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

        binding.roomsBtnBack.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        binding.roomsBtnEdit.setOnClickListener(_view_ -> {
            if (id == null || id == -1) {
                Common.showCustomSnackBar("PLEASE SELECT A ROOM", requireContext(), binding.getRoot());
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                NavHostFragment.findNavController(this).navigate(R.id.action_fragmentListRoom_to_fragmentEditRoom, bundle);
            }
        });

        binding.roomsBtnDelete.setOnClickListener(_view_ -> {
            if (id != null && id != -1) {
                List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue();
                if (roomObservables != null) {
                    Optional<RoomObservable> optionalRoomObservable = roomObservables.stream().filter
                            (roomObservable -> roomObservable.getId().equals(id)).findFirst();
                    if (optionalRoomObservable.isPresent()) {
                        String warningTag = "FragmentListRoom Warning";
                        String successTag = "FragmentListRoom Success";
                        String failureTag = "FragmentListRoom Failure";
                        Common.onDeleteRecyclerViewItemClickHandler(roomViewModel, optionalRoomObservable.get()
                                , warningTag
                                , successTag
                                , failureTag
                                , binding.getRoot()
                                , requireActivity()
                                , dismissPopupWindowLoading);
                    }
                }
            } else {
                Common.showCustomSnackBar("PLEASE SELECT A ROOM", requireContext(), binding.getRoot());
            }
        });

        binding.roomsFilter.setOnClickListener(_view_ -> {
            Common.hideKeyboard(requireActivity());

            PopupWindowFilterRoom popupWindowFilterRoom = PopupWindowFilterRoom.newOne(getLayoutInflater(), binding.getRoot(), binding.roomsSearchView, getViewLifecycleOwner());
            int offsetX = 0;
            int offsetY = 10;
            popupWindowFilterRoom.showAsDropDown(binding.roomsFilter, offsetX, offsetY);
            popupWindowFilterRoom.setOnDismissListener(() ->
            {
                YoYo
                        .with(Techniques.ZoomIn).duration(400).playOn(binding.roomsFilter);
                Glide
                        .with(this)
                        .load(AppCompatResources.getDrawable(requireContext(), R.drawable.img_filter_v1))
                        .into(binding.roomsFilter);
            });

            {
                YoYo
                        .with(Techniques.ZoomIn).duration(400).playOn(binding.roomsFilter);
                Glide
                        .with(this)
                        .load(AppCompatResources.getDrawable(requireContext(), R.drawable.img_filter_v2))
                        .into(binding.roomsFilter);
            }

            binding.roomsSearchView.clearFocus();
        });

        Common.setupSearchFeatureInListLikeFragment(searchProcessor
                , binding.roomsSearchView
                , new SearchStrategyRoom(requireActivity())
                , onSearchRoomObservablesConsumer, roomAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        dismissPopupWindowLoading.set(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        roomViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
        searchProcessor = null;
        dismissPopupWindowLoading.set(true);
        onSearchRoomObservablesConsumer = null;
        Common.searchViewOnFocusChangeForwardingHandler = null;
    }

    @Override
    public void onRoomClick(RoomObservable roomObservable) {
        if (isFstTime) {
            isFstTime = false;
            id = roomObservable.getId();
        } else {
            id = roomObservable.getId().equals(id) ? -1 : roomObservable.getId();
        }
    }

}