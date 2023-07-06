package com.example.hotelmanagement.fragments.lists;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.common.Common;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.RoomAdapter;
import com.example.hotelmanagement.databinding.FragmentRoomsBinding;
import com.example.hotelmanagement.dialogs.DialogFragmentFailure;
import com.example.hotelmanagement.dialogs.DialogFragmentSuccess;
import com.example.hotelmanagement.dialogs.DialogFragmentWarning;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.popupwindows.PopupWindowFilterRoom;
import com.example.hotelmanagement.viewmodels.RoomViewModel;
import com.example.search.SearchProcessor;
import com.example.search.SearchStrategyRoom;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentRooms extends Fragment implements RoomAdapter.RoomListener {

    private static Integer id;
    private Boolean isFstTime;

    private RoomViewModel roomViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentRoomsBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<RoomObservable>> onSearchRoomObservablesConsumer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoomsBinding.inflate(inflater, container, false);
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
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRooms_to_fragmentAddRoom);
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
        binding.roomsBtnBook.setVisibility(View.INVISIBLE);
        binding.roomsBtnEdit.setVisibility(View.INVISIBLE);
        binding.roomsBtnDelete.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> {
            if (binding != null && binding.roomsBtnAdd.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomsBtnAdd);
            }
            if (binding != null && binding.roomsBtnBook.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomsBtnBook.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomsBtnBook);
            }
            if (binding != null && binding.roomsBtnEdit.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomsBtnEdit.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomsBtnEdit);
            }
            if (binding != null && binding.roomsBtnDelete.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomsBtnDelete.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomsBtnDelete);
            }
        };
        binding.roomsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.roomsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.roomsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.roomsBtnAdd);
            }
            if (binding.roomsBtnBook.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.roomsBtnBook.setVisibility(View.VISIBLE)).playOn(binding.roomsBtnBook);
            }
            if (binding.roomsBtnEdit.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.roomsBtnEdit.setVisibility(View.VISIBLE)).playOn(binding.roomsBtnEdit);
            }
            if (binding.roomsBtnDelete.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.roomsBtnDelete.setVisibility(View.VISIBLE)).playOn(binding.roomsBtnDelete);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

        binding.roomsBtnBack.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        binding.roomsBtnEdit.setOnClickListener(_view_ -> {
            if (id == -1) {
                Toast.makeText(requireContext(), "PLEASE SELECT A ROOM", Toast.LENGTH_LONG).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRooms_to_fragmentEditRoom, bundle);
            }
        });

        binding.roomsBtnDelete.setOnClickListener(_view_ -> {
            if (id != null && id != -1) {
                List<RoomObservable> roomObservables = roomViewModel.getModelState().getValue();
                if (roomObservables != null) {
                    Optional<RoomObservable> optionalRoomObservable = roomObservables.stream().filter
                            (roomObservable -> roomObservable.getId().equals(id)).findFirst();
                    if (optionalRoomObservable.isPresent()) {
                        Consumer<DialogFragmentWarning.Answer> onCancelHandler = answer -> {
                            if (answer == DialogFragmentWarning.Answer.YES) {
                                roomViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                                    if (getActivity() != null) {
                                        requireActivity().runOnUiThread(() -> {
                                            if (apolloErrors != null) {
                                                DialogFragmentFailure.newOne(getParentFragmentManager()
                                                        , "FragmentRooms Failure", apolloErrors.get(0).getMessage());
                                            }
                                        });
                                    }
                                };
                                roomViewModel.onSuccessCallback = () -> {
                                    if (getActivity() != null) {
                                        requireActivity().runOnUiThread(() -> {
                                            String message = "Success: Your item has been deleted successfully.";
                                            DialogFragmentSuccess.newOne(getParentFragmentManager()
                                                    , "FragmentRooms Success", message);
                                        });
                                    }
                                };
                                roomViewModel.onFailureCallback = null;
                                roomViewModel.delete(optionalRoomObservable.get());
                            }
                        };
                        String message = "Caution: Deleting this item will result in permanent removal from the system.";
                        DialogFragmentWarning.newOne(getParentFragmentManager(), "FragmentRooms Warning", message, onCancelHandler);
                    }
                }
            }
        });

        binding.roomsFilter.setOnClickListener(_view_ -> {
            Common.hideKeyboard(requireActivity());
            PopupWindowFilterRoom popupWindowFilterRoom = PopupWindowFilterRoom.newOne(getLayoutInflater(), binding.getRoot(), binding.roomsSearchView);
            int offsetX = 0;
            int offsetY = 10;
            popupWindowFilterRoom.showAsDropDown(binding.roomsFilter, offsetX, offsetY);
            binding.roomsSearchView.clearFocus();
        });

        Common.setupSearchFeatureInListLikeFragment(searchProcessor
                , binding.roomsSearchView
                , new SearchStrategyRoom(requireActivity())
                , onSearchRoomObservablesConsumer, roomAdapter);

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