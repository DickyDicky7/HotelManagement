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
import com.example.hotelmanagement.databinding.SuccessDialogBinding;

public class SuccessDialogFragment extends DialogFragment {

    @NonNull
    protected String message;

    public SuccessDialogFragment(@NonNull String message) {
        this.message = message;
    }

    public static void newOne(@NonNull FragmentManager fragmentManager, @NonNull String tag, @NonNull String message) {
        SuccessDialogFragment successDialogFragment = new SuccessDialogFragment(message);
        successDialogFragment.showNow(fragmentManager, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        SuccessDialogBinding binding = SuccessDialogBinding.inflate(getLayoutInflater());
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
