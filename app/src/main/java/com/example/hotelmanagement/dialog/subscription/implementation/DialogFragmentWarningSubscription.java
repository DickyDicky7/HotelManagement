package com.example.hotelmanagement.dialog.subscription.implementation;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.dialog.implementation.DialogFragmentWarning;
import com.example.hotelmanagement.dialog.subscription.abstraction.DialogFragmentSubscription;

import java.util.function.Consumer;

public class DialogFragmentWarningSubscription extends DialogFragmentSubscription {

    @NonNull
    public Consumer<DialogFragmentWarning.Answer> onCancelHandler;

    public DialogFragmentWarningSubscription(@NonNull String tag, @NonNull String message, @NonNull Consumer<DialogFragmentWarning.Answer> onCancelHandler) {
        super(tag, message);
        this.onCancelHandler = onCancelHandler;
    }

}
