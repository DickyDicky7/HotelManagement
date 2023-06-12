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

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.GuestAdapter;
import com.example.hotelmanagement.databinding.FragmentGuestsBinding;
import com.example.hotelmanagement.viewmodels.GuestViewModel;

public class FragmentGuests extends Fragment {

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

        binding.guestsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentGuests_to_fragmentAddGuest);
        });

        GuestAdapter guestAdapter = new GuestAdapter(requireActivity());
        binding.guestsRecyclerView.setAdapter(guestAdapter);
        binding.guestsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        GuestViewModel guestViewModel = new ViewModelProvider(requireActivity()).get(GuestViewModel.class);
        guestViewModel.getModelState().observe(getViewLifecycleOwner(), updatedGuestObservables -> {
            guestAdapter.Clear();
            guestAdapter.Fill(updatedGuestObservables);
        });

        binding.guestsBtnAdd.setVisibility(View.INVISIBLE);
        int delayMilliseconds = 3000;
        handler = new Handler();
        timeoutCallback = () -> binding.guestsBtnAdd.setVisibility(View.INVISIBLE);
        binding.guestsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            binding.guestsBtnAdd.setVisibility(View.VISIBLE);
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

}