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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.RoomAdapter;
import com.example.hotelmanagement.databinding.FragmentRoomsBinding;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.RoomViewModel;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.FadeInLeftAnimator;

public class FragmentRooms extends Fragment implements RoomAdapter.RoomListener {

    private Handler handler;
    private Runnable timeoutCallback;
    private FragmentRoomsBinding binding;

    private int id = -1;

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

        binding.roomsRecyclerView.setItemAnimator(new FadeInLeftAnimator());

        RoomAdapter roomAdapter = new RoomAdapter(requireActivity(), this);
        binding.roomsRecyclerView.setAdapter(new ScaleInAnimationAdapter(roomAdapter));
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireContext());
        flexboxLayoutManager.setAlignItems(AlignItems.CENTER);
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        binding.roomsRecyclerView.setLayoutManager(flexboxLayoutManager);
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


        binding.roomsBtnEdit.setOnClickListener(_view_ -> {
            if (id == -1){
                Toast.makeText(getContext(),"Chua chon phong ",Toast.LENGTH_LONG).show();
            }
            else {
                Bundle bundle = new Bundle();
                bundle.putInt("id",id);
                NavHostFragment.findNavController(this).navigate(R.id.action_fragmentRooms_to_fragmentEditRoom,bundle);
            }
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
    public void onRoomClick(RoomObservable roomObservable) {
        id = roomObservable.getId();
    }

}