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
import com.example.hotelmanagement.adapters.GuestAdapter;
import com.example.hotelmanagement.databinding.FragmentGuestsBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;
import com.example.hotelmanagement.dialog.SuccessDialogFragment;
import com.example.hotelmanagement.dialog.WarningDialogFragment;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewmodels.GuestViewModel;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentGuests extends Fragment implements GuestAdapter.GuestListener {

    private GuestViewModel guestViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentGuestsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentGuestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.guestsSearchView.setIconifiedByDefault(false);
        EditText editText = binding.guestsSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = binding.guestsSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView closeButton = binding.guestsSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
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

        binding.guestsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentGuests_to_fragmentAddGuest);
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
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.guestsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.guestsBtnAdd);
            }
        };
        binding.guestsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.guestsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.guestsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.guestsBtnAdd);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

        binding.guestsBtnBack.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        guestViewModel = null;
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
    }

    @Override
    public void onJustGuestClick(GuestObservable guestObservable) {

    }

    @Override
    public void onEditGuestClick(GuestObservable guestObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", guestObservable.getId());
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentGuests_to_fragmentEditGuest, bundle);
    }

    @Override
    public void onDeleGuestClick(GuestObservable guestObservable) {
        Consumer<WarningDialogFragment.Answer> onCancelHandler = answer -> {
            if (answer == WarningDialogFragment.Answer.YES) {
                guestViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                FailureDialogFragment.newOne(getParentFragmentManager()
                                        , "FragmentGuests Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                guestViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            String message = "Success: Your item has been deleted successfully.";
                            SuccessDialogFragment.newOne(getParentFragmentManager()
                                    , "FragmentGuests Success", message);
                        });
                    }
                };
                guestViewModel.onFailureCallback = null;
                guestViewModel.delete(guestObservable);
            }
        };
        String message = "Caution: Deleting this item will result in permanent removal from the system.";
        WarningDialogFragment.newOne(getParentFragmentManager(), "FragmentGuests Warning", message, onCancelHandler);
    }

}