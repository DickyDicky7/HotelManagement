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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.common.Common;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.RoomKindAdapter;
import com.example.hotelmanagement.databinding.FragmentRoomKindsBinding;
import com.example.hotelmanagement.dialogs.DialogFragmentFailure;
import com.example.hotelmanagement.dialogs.DialogFragmentSuccess;
import com.example.hotelmanagement.dialogs.DialogFragmentWarning;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.search.SearchProcessor;
import com.example.search.SearchStrategyRoomKind;

import java.util.List;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentRoomKinds extends Fragment implements RoomKindAdapter.RoomKindListener {

    private RoomKindViewModel roomKindViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentRoomKindsBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<RoomKindObservable>> onSearchRoomKindObservablesConsumer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRoomKindsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Common.beautifySearchView(binding.roomKindsSearchView, requireContext());

        binding.roomKindsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRoomKinds_to_fragmentAddRoomKind);
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
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.roomKindsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.roomKindsBtnAdd);
            }
        };
        binding.roomKindsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.roomKindsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.roomKindsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.roomKindsBtnAdd);
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
    public void onDestroyView() {
        super.onDestroyView();
        roomKindViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
        searchProcessor = null;
        onSearchRoomKindObservablesConsumer = null;
        Common.searchViewOnFocusChangeForwardingHandler = null;
    }

    @Override
    public void onJustRoomKindClick(RoomKindObservable roomKindObservable) {

    }

    @Override
    public void onEditRoomKindClick(RoomKindObservable roomKindObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", roomKindObservable.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRoomKinds_to_fragmentEditRoomKind, bundle);
    }

    @Override
    public void onDeleRoomKindClick(RoomKindObservable roomKindObservable) {
        Consumer<DialogFragmentWarning.Answer> onCancelHandler = answer -> {
            if (answer == DialogFragmentWarning.Answer.YES) {
                roomKindViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                DialogFragmentFailure.newOne(getParentFragmentManager()
                                        , "FragmentRoomKinds Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                roomKindViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            String message = "Success: Your item has been deleted successfully.";
                            DialogFragmentSuccess.newOne(getParentFragmentManager()
                                    , "FragmentRoomKinds Success", message);
                        });
                    }
                };
                roomKindViewModel.onFailureCallback = null;
                roomKindViewModel.delete(roomKindObservable);
            }
        };
        String message = "Caution: Deleting this item will result in permanent removal from the system.";
        DialogFragmentWarning.newOne(getParentFragmentManager(), "FragmentRoomKinds Warning", message, onCancelHandler);
    }

}