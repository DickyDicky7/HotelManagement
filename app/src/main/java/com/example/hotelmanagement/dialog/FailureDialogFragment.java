package com.example.hotelmanagement.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.databinding.FailureDialogBinding;

public class FailureDialogFragment extends DialogFragment {

    @NonNull
    protected String message;

    public FailureDialogFragment(@NonNull String message) {
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        FailureDialogBinding binding = FailureDialogBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());
        AlertDialog alertDialog = builder.create();
        binding.notiFailMsg.setText(message);
        binding.notiFailOK.setOnClickListener(view -> alertDialog.cancel());
        binding.notiFailBtnclose.setOnClickListener(view -> alertDialog.cancel());
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        YoYo.with(Techniques.Bounce).duration(1000).playOn(alertDialog.getWindow().getDecorView());
        return alertDialog;
    }

}
