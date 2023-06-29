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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.RoomKindAdapter;
import com.example.hotelmanagement.databinding.FragmentRoomKindsBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;
import com.example.hotelmanagement.dialog.SuccessDialogFragment;
import com.example.hotelmanagement.dialog.WarningDialogFragment;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.search.RoomKindSearchStrategy;
import com.example.search.SearchProcessor;

import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentRoomKinds extends Fragment implements RoomKindAdapter.RoomKindListener {

    private RoomKindViewModel roomKindViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentRoomKindsBinding binding;

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

        binding.roomKindsSearchView.setIconifiedByDefault(false);
        EditText editText = binding.roomKindsSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = binding.roomKindsSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView closeButton = binding.roomKindsSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        editText.setCursorVisible(true);
        editText.setTextColor(Color.GRAY);
        editText.setHintTextColor(Color.GRAY);
        searchIcon.setColorFilter(Color.GRAY);
        closeButton.setColorFilter(Color.GRAY);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editText.setOnFocusChangeListener((_view_, isFocused) -> {
            if (isFocused) {
                editText.setTextColor(requireContext().getColor(R.color.bluegray_900));
                editText.setHintTextColor(requireContext().getColor(R.color.bluegray_900));
                searchIcon.setColorFilter(requireContext().getColor(R.color.bluegray_900));
                closeButton.setColorFilter(requireContext().getColor(R.color.bluegray_900));
            } else {
                editText.setTextColor(Color.GRAY);
                editText.setHintTextColor(Color.GRAY);
                searchIcon.setColorFilter(Color.GRAY);
                closeButton.setColorFilter(Color.GRAY);
            }
        });

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

        SearchProcessor searchProcessor = new SearchProcessor(new RoomKindSearchStrategy(roomKindViewModel));
        binding.roomKindsBtnHelp.setOnClickListener(_view_ -> {
            searchProcessor.processSearch(binding.roomKindsSearchView.getQuery().toString());
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        roomKindViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
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
        Consumer<WarningDialogFragment.Answer> onCancelHandler = answer -> {
            if (answer == WarningDialogFragment.Answer.YES) {
                roomKindViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                FailureDialogFragment.newOne(getParentFragmentManager()
                                        , "FragmentRoomKinds Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                roomKindViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            String message = "Success: Your item has been deleted successfully.";
                            SuccessDialogFragment.newOne(getParentFragmentManager()
                                    , "FragmentRoomKinds Success", message);
                        });
                    }
                };
                roomKindViewModel.onFailureCallback = null;
                roomKindViewModel.delete(roomKindObservable);
            }
        };
        String message = "Caution: Deleting this item will result in permanent removal from the system.";
        WarningDialogFragment.newOne(getParentFragmentManager(), "FragmentRoomKinds Warning", message, onCancelHandler);
    }

}