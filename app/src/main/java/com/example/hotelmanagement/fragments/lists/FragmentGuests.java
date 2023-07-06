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
import com.example.hotelmanagement.adapters.GuestAdapter;
import com.example.hotelmanagement.databinding.FragmentGuestsBinding;
import com.example.hotelmanagement.dialogs.DialogFragmentFailure;
import com.example.hotelmanagement.dialogs.DialogFragmentSuccess;
import com.example.hotelmanagement.dialogs.DialogFragmentWarning;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.viewmodels.GuestViewModel;
import com.example.search.SearchProcessor;
import com.example.search.SearchStrategyGuest;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.List;
import java.util.function.Consumer;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentGuests extends Fragment implements GuestAdapter.GuestListener {

    private GuestViewModel guestViewModel;
    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentGuestsBinding binding;

    private SearchProcessor searchProcessor;
    private Consumer<List<GuestObservable>> onSearchGuestObservablesConsumer;

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

        Common.beautifySearchView(binding.guestsSearchView, requireContext());

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

        Common.setupSearchFeatureInListLikeFragment(searchProcessor
                , binding.guestsSearchView
                , new SearchStrategyGuest(requireActivity())
                , onSearchGuestObservablesConsumer, guestAdapter);

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
        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentGuests_to_fragmentEditGuest, bundle);
    }

    @Override
    public void onDeleGuestClick(GuestObservable guestObservable) {
        Consumer<DialogFragmentWarning.Answer> onCancelHandler = answer -> {
            if (answer == DialogFragmentWarning.Answer.YES) {
                guestViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                DialogFragmentFailure.newOne(getParentFragmentManager()
                                        , "FragmentGuests Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                guestViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            String message = "Success: Your item has been deleted successfully.";
                            DialogFragmentSuccess.newOne(getParentFragmentManager()
                                    , "FragmentGuests Success", message);
                        });
                    }
                };
                guestViewModel.onFailureCallback = null;
                guestViewModel.delete(guestObservable);
            }
        };
        String message = "Caution: Deleting this item will result in permanent removal from the system.";
        DialogFragmentWarning.newOne(getParentFragmentManager(), "FragmentGuests Warning", message, onCancelHandler);
    }

}