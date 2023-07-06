package com.example.hotelmanagement.popupwindows;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.databinding.PopupWindowFilterRoomBinding;

public class PopupWindowFilterRoom extends PopupWindow {

    protected static int HEIGHT = 650;
    @NonNull
    protected SearchView searchView;
    @NonNull
    PopupWindowFilterRoomBinding binding;

    public PopupWindowFilterRoom(@NonNull PopupWindowFilterRoomBinding binding, int w, int h, boolean focusable, @NonNull SearchView searchView) {
        super(binding.getRoot(), w, h, focusable);
        this.binding = binding;
        this.searchView = searchView;
    }

    @NonNull
    public static PopupWindowFilterRoom newOne(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, @NonNull SearchView searchView) {
        boolean attachToParent = false;
        PopupWindowFilterRoomBinding binding = PopupWindowFilterRoomBinding
                .inflate(inflater, parent, attachToParent);
        boolean focusable = true;
        return new PopupWindowFilterRoom
                (binding, parent.getMeasuredWidth(), HEIGHT, focusable, searchView);
    }

    @Override
    public void showAsDropDown(View anchor) {
        YoYo.with(Techniques.SlideInDown).duration(300).playOn(binding.getRoot());
        super.showAsDropDown(anchor);
    }

    @Override
    public void showAsDropDown(View anchor, int offsetX, int offsetY) {
        YoYo.with(Techniques.SlideInDown).duration(300).playOn(binding.getRoot());
        super.showAsDropDown(anchor, offsetX, offsetY);
    }

    @Override
    public void showAsDropDown(View anchor, int offsetX, int offsetY, int gravity) {
        YoYo.with(Techniques.SlideInDown).duration(300).playOn(binding.getRoot());
        super.showAsDropDown(anchor, offsetX, offsetY, gravity);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        YoYo.with(Techniques.SlideInDown).duration(300).playOn(binding.getRoot());
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void dismiss() {
        YoYo.with(Techniques.SlideOutUp).duration(300).playOn(binding.getRoot());
        super.dismiss();
    }

}
