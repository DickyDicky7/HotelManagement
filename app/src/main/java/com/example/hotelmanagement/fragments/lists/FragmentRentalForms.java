package com.example.hotelmanagement.fragments.lists;

import android.graphics.Color;
import android.os.Bundle;
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
import com.example.hotelmanagement.adapters.RentalFormAdapter;
import com.example.hotelmanagement.databinding.FragmentRentalFormsBinding;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;

public class FragmentRentalForms extends Fragment {

    private FragmentRentalFormsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRentalFormsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

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

        RentalFormAdapter rentalFormAdapter = new RentalFormAdapter(requireActivity());
        binding.rentalFormsRecyclerView.setAdapter(rentalFormAdapter);
        binding.rentalFormsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RentalFormViewModel rentalFormViewModel = new ViewModelProvider(requireActivity()).get(RentalFormViewModel.class);
        rentalFormViewModel.getModelState().observe(getViewLifecycleOwner(), updatedRentalFormObservables -> {
            rentalFormAdapter.Clear();
            rentalFormAdapter.Fill(updatedRentalFormObservables);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}