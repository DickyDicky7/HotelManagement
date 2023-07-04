package com.example.hotelmanagement.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
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
import com.example.hotelmanagement.databinding.DialogWarningBinding;

import java.util.function.Consumer;

public class DialogFragmentWarning extends DialogFragment {

    @NonNull
    protected String message;
    @NonNull
    protected Answer answers;
    @NonNull
    protected Consumer<Answer> onCancelHandler;

    public DialogFragmentWarning(@NonNull String message, @NonNull Consumer<Answer> onCancelHandler) {
        this.message = message;
        this.answers = Answer.HUH;
        this.onCancelHandler = onCancelHandler;
    }

    public static void newOne(@NonNull FragmentManager fragmentManager, @NonNull String tag, @NonNull String message, @NonNull Consumer<Answer> onCancelHandler) {
        DialogFragmentWarning dialogFragmentWarning = new DialogFragmentWarning(message, onCancelHandler);
        dialogFragmentWarning.showNow(fragmentManager, tag);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        DialogWarningBinding binding = DialogWarningBinding.inflate(getLayoutInflater());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(binding.getRoot());
        AlertDialog alertDialog = builder.create();

        binding.notiWarningMsg.setText(message);

        binding.notiWarningOK.setOnClickListener(view -> {
            answers = Answer.YES;
            alertDialog.cancel();
        });

        binding.notiWarningBtnClose.setOnClickListener(view -> {
            answers = Answer.NAH;
            alertDialog.cancel();
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        YoYo.with(Techniques.Bounce).duration(1000).playOn(alertDialog.getWindow().getDecorView());

        return alertDialog;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        onCancelHandler.accept(answers);
    }

    public enum Answer {
        YES,
        NAH,
        HUH,
    }

}
