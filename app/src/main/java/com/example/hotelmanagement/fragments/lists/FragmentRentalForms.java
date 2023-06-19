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
import com.example.hotelmanagement.adapters.RentalFormAdapter;
import com.example.hotelmanagement.databinding.FragmentRentalFormsBinding;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentRentalForms extends Fragment implements RentalFormAdapter.RentalFormListener {

    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentRentalFormsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRentalFormsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rentalFormsSearchView.setIconifiedByDefault(false);
        EditText editText = binding.rentalFormsSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = binding.rentalFormsSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView closeButton = binding.rentalFormsSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        editText.setCursorVisible(false);
        editText.setTextColor(Color.GRAY);
        editText.setHintTextColor(Color.GRAY);
        searchIcon.setColorFilter(Color.GRAY);
        closeButton.setColorFilter(Color.GRAY);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editText.setOnFocusChangeListener((_view_, isFocused) -> {
            if (isFocused) {
                editText.setTextColor(getResources().getColor(R.color.bluegray_900));
                editText.setHintTextColor(getResources().getColor(R.color.bluegray_900));
                searchIcon.setColorFilter(getResources().getColor(R.color.bluegray_900));
                closeButton.setColorFilter(getResources().getColor(R.color.bluegray_900));
            } else {
                editText.setTextColor(Color.GRAY);
                editText.setHintTextColor(Color.GRAY);
                searchIcon.setColorFilter(Color.GRAY);
                closeButton.setColorFilter(Color.GRAY);
            }
        });

        binding.rentalFormsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRentalForms_to_fragmentAddRentalForm);
        });

        binding.rentalFormsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        RentalFormAdapter rentalFormAdapter = new RentalFormAdapter(requireActivity(),this);
        binding.rentalFormsRecyclerView.setAdapter(new ScaleInAnimationAdapter(rentalFormAdapter));
        binding.rentalFormsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RentalFormViewModel rentalFormViewModel = new ViewModelProvider(requireActivity()).get(RentalFormViewModel.class);
        rentalFormViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRentalFormObservables -> {
            rentalFormAdapter.Clear();
            rentalFormAdapter.Fill(updatedRentalFormObservables);
        });

        binding.rentalFormsBtnAdd.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> {
            if (binding != null && binding.rentalFormsBtnAdd.getVisibility() != View.INVISIBLE) {
                YoYo.with(Techniques.FadeOutDown).duration(500).onEnd(animator -> {
                    if (binding != null) {
                        binding.rentalFormsBtnAdd.setVisibility(View.INVISIBLE);
                    }
                }).playOn(binding.rentalFormsBtnAdd);
            }
        };
        binding.rentalFormsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            if (binding.rentalFormsBtnAdd.getVisibility() != View.VISIBLE) {
                YoYo.with(Techniques.FadeInUp).duration(500).onStart(animator -> binding.rentalFormsBtnAdd.setVisibility(View.VISIBLE)).playOn(binding.rentalFormsBtnAdd);
            }
            handler.postDelayed(timeoutCallback, delayMilliseconds);
            return false;
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacks(timeoutCallback);
        handler = null;
        timeoutCallback = null;
    }

    @Override
    public void onRentalFormClick(RentalFormObservable rentalFormObservable) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", rentalFormObservable.getId());

        NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRentalForms_to_fragmentEditRentalForm, bundle);
    }
}