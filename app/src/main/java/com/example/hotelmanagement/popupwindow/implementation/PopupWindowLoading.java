package com.example.hotelmanagement.popupwindow.implementation;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.databinding.PopupWindowLoadingBinding;

public class PopupWindowLoading extends PopupWindow {

    protected static final int W = Resources.getSystem().getDisplayMetrics().widthPixels;
    protected static final int H = Resources.getSystem().getDisplayMetrics().heightPixels;

    @NonNull
    protected PopupWindowLoadingBinding binding;

    public PopupWindowLoading(@NonNull PopupWindowLoadingBinding binding, int w, int h, boolean focusable) {
        super(binding.getRoot(), w, h, focusable);
        this.binding = binding;
    }

    @NonNull
    public static PopupWindowLoading newOne(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        boolean focusable = false;
        return
                newOne(inflater, parent, W, H, focusable);
    }

    @NonNull
    public static PopupWindowLoading newOne(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent, int w, int h, boolean focusable) {
        boolean attachToParent = false;
        return
                new PopupWindowLoading(PopupWindowLoadingBinding.inflate(inflater, parent, attachToParent), w, h, focusable);
    }

    protected void showAnimation() {
        YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.getRoot());
    }

    protected void dismissAnimation() {
        YoYo.with(Techniques.FadeOut).duration(300).playOn(binding.getRoot());
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
