package com.example.hotelmanagement.popupwindow.implementation;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.PopupWindowFilterRoomBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PopupWindowFilterRoom extends PopupWindow {

    protected static final int W = Resources.getSystem().getDisplayMetrics().widthPixels;
    protected static final int H = 650;
    @Nullable
    protected static String selectQuantity;
    @Nullable
    protected static String selectOccupied;
    @Nullable
    protected static String selectRoomKindName;
    protected int normalTextColor;
    protected int selectTextColor;
    protected int normalBackground;
    protected int selectBackground;
    @NonNull
    protected SearchView searchView;
    @NonNull
    protected PopupWindowFilterRoomBinding binding;

    @Nullable
    protected TextView LastClickedTextView;
    @Nullable
    protected AppCompatButton LastClickedAppCompatButton;

    public PopupWindowFilterRoom(@NonNull PopupWindowFilterRoomBinding binding, int w, int h, boolean focusable, @NonNull SearchView searchView, @NonNull LifecycleOwner LifecycleOwner) {
        super(binding.getRoot(), w, h, focusable);
        this.binding = binding;
        this.searchView = searchView;

        ArrayAdapter<String> roomKindArrayAdapter = new ArrayAdapter<>(searchView.getContext(), R.layout.item_spinner, new ArrayList<>());
        roomKindArrayAdapter.setDropDownViewResource(R.layout.item_spinner);

        binding
                .selectRoomKindSpinner
                .setAdapter(roomKindArrayAdapter);

        normalTextColor = searchView.getContext().getColor(
                R.color.gray_500);
        selectTextColor = searchView.getContext().getColor(
                R.color.white_200);

        normalBackground =
                R.drawable.rectangle_bg_gray_300_radius_13;
        selectBackground =
                R.drawable.rectangle_bg_indigo_100_radius_13;

        RoomKindViewModel roomKindViewModel = new ViewModelProvider((FragmentActivity) searchView.getContext()).get(RoomKindViewModel.class);
        roomKindViewModel.getModelState().observe(LifecycleOwner, updatedRoomKindObservables -> {
            roomKindArrayAdapter.clear();
            roomKindArrayAdapter.addAll(updatedRoomKindObservables.stream().map(RoomKindObservable::getName).toArray(String[]::new));
            if (selectRoomKindName == null) {
                binding.selectRoomKindSpinner.setSelection(0);
            } else {
                binding.selectRoomKindSpinner.setSelection(roomKindArrayAdapter.getPosition(
                        selectRoomKindName
                                .substring(2)), true);
            }
        });

        binding.selectRoomKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectRoomKindName = "k " + adapterView.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectRoomKindName = "";
            }
        });

        try {
            for (Field field : binding.getClass().getFields()) {
                field.setAccessible(true);
                if (field.getName().startsWith("textViewQuantity")) {
                    TextView textView = (TextView) field.get(binding);
                    if (textView != null) {
                        textView.setTextColor(normalTextColor);
                        textView.setBackgroundResource(normalBackground);
                        String quantity = "q " + field.getName().substring(16);
                        textView.setOnClickListener(view -> {
                            selectQuantity = quantity;
                            if (LastClickedTextView != null) {
                                LastClickedTextView.setTextColor(normalTextColor);
                                LastClickedTextView.setBackgroundResource(normalBackground);
                            }
                            LastClickedTextView = textView;
                            textView.setTextColor(selectTextColor);
                            textView.setBackgroundResource(selectBackground);
                        });
                        if (quantity.equals(selectQuantity)) {
                            textView.performClick();
                        }
                    }
                } else if (field.getName().startsWith("buttonAvailable")) {
                    AppCompatButton appCompatButton = (AppCompatButton) field.get(binding);
                    if (appCompatButton != null) {
                        appCompatButton.setTextColor(normalTextColor);
                        appCompatButton.setBackgroundResource(normalBackground);
                        String occupied = "o " + (field.getName().equals("buttonAvailableYes") ? "yes" : "no");
                        appCompatButton.setOnClickListener(view -> {
                            selectOccupied = occupied;
                            if (LastClickedAppCompatButton != null) {
                                LastClickedAppCompatButton.setTextColor(normalTextColor);
                                LastClickedAppCompatButton.setBackgroundResource(normalBackground);
                            }
                            LastClickedAppCompatButton = appCompatButton;
                            appCompatButton.setTextColor(selectTextColor);
                            appCompatButton.setBackgroundResource(selectBackground);
                        });
                        if (occupied.equals(selectOccupied)) {
                            appCompatButton.performClick();
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (selectQuantity == null && selectOccupied == null) {
            binding.textViewQuantity01.performClick();
            binding.buttonAvailableYes.performClick();
        }

        binding.buttonDone.setOnClickListener(view -> {
            dismiss();
            searchView.setQuery(selectRoomKindName + ", " + selectQuantity + ", " + selectOccupied, false);
        });

    }

    @NonNull
    public static PopupWindowFilterRoom newOne(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull SearchView searchView, @NonNull LifecycleOwner LifecycleOwner) {
        boolean focusable = true;
        return
                newOne(inflater, parent, searchView, LifecycleOwner, W, H, focusable);
    }

    @NonNull
    public static PopupWindowFilterRoom newOne(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull SearchView searchView, @NonNull LifecycleOwner LifecycleOwner, int w, int h, boolean focusable) {
        boolean attachToParent = false;
        return
                new PopupWindowFilterRoom(PopupWindowFilterRoomBinding.inflate(inflater, parent, attachToParent), w, h, focusable, searchView, LifecycleOwner);
    }

    protected void showAnimation() {
        YoYo.with(Techniques.ZoomIn).duration(400).playOn(binding.getRoot());
    }

    protected void dismissAnimation() {
        YoYo.with(Techniques.ZoomIn).duration(400).playOn(binding.getRoot());
    }

    @Override
    public void showAsDropDown(View anchor) {
        showAnimation();
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(
            View anchor,
            int offsetX,
            int offsetY) {
        showAnimation();
        super.showAsDropDown(anchor, offsetX, offsetY);
    }

    @Override
    public void showAsDropDown(
            View anchor,
            int offsetX,
            int offsetY,
            int gravity) {
        showAnimation();
        super.showAsDropDown(anchor, offsetX, offsetY, gravity);
    }

    @Override
    public void showAtLocation(
            View parent,
            int gravity,
            int x,
            int y) {
        showAnimation();
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void dismiss() {
        dismissAnimation();
        super.dismiss();
    }

}
