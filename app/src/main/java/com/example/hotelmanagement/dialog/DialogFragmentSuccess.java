package com.example.hotelmanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.databinding.DialogSuccessBinding;

public class DialogFragmentSuccess extends DialogFragment {

    @NonNull
    protected String message;

    public DialogFragmentSuccess(@NonNull String message) {
        this.message = message;
    }

    public static void newOne(@NonNull FragmentManager fragmentManager, @NonNull String tag, @NonNull String message) {
        DialogFragmentSuccess dialogFragmentSuccess = new DialogFragmentSuccess(message);
        dialogFragmentSuccess.showNow(fragmentManager, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogSuccessBinding binding = DialogSuccessBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());
        AlertDialog alertDialog = builder.create();
        binding.notiSuccessMsg.setText(message);
        binding.notiSuccessOK.setOnClickListener(view -> alertDialog.cancel());
        binding.notiSuccessBtnClose.setOnClickListener(view -> alertDialog.cancel());
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        YoYo.with(Techniques.Bounce).duration(1000).playOn(alertDialog.getWindow().getDecorView());
        return alertDialog;
    }

}
