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
import com.example.hotelmanagement.adapters.RoomAdapter;
import com.example.hotelmanagement.databinding.FragmentRoomsBinding;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

public class FragmentRooms extends Fragment {

    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentRoomsBinding binding;

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

        binding.roomsSearchView.setIconifiedByDefault(false);
        EditText editText = binding.roomsSearchView.findViewById(androidx.appcompat.R.id.search_src_text);
        ImageView searchIcon = binding.roomsSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ImageView closeButton = binding.roomsSearchView.findViewById(androidx.appcompat.R.id.search_close_btn);
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

        binding.roomsBtnAdd.setOnClickListener(_view_ -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRooms_to_fragmentAddRoom);
        });

        RoomAdapter roomAdapter = new RoomAdapter(requireActivity());
        binding.roomsRecyclerView.setAdapter(roomAdapter);
        binding.roomsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        RoomViewModel roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
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
            binding.roomsBtnAdd.setVisibility(View.INVISIBLE);
            binding.roomsBtnBook.setVisibility(View.INVISIBLE);
            binding.roomsBtnEdit.setVisibility(View.INVISIBLE);
            binding.roomsBtnDelete.setVisibility(View.INVISIBLE);
        };
        binding.roomsRecyclerView.setOnTouchListener((_view_, motionEvent) -> {
            handler.removeCallbacks(timeoutCallback);
            binding.roomsBtnAdd.setVisibility(View.VISIBLE);
            binding.roomsBtnBook.setVisibility(View.VISIBLE);
            binding.roomsBtnEdit.setVisibility(View.VISIBLE);
            binding.roomsBtnDelete.setVisibility(View.VISIBLE);
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