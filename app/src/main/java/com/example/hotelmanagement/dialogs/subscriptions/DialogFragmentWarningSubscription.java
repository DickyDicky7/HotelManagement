package com.example.hotelmanagement.dialogs.subscriptions;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.dialogs.DialogFragmentWarning;

import java.util.function.Consumer;

public class DialogFragmentWarningSubscription extends DialogFragmentSubscription {

    @NonNull
    public Consumer<DialogFragmentWarning.Answer> onCancelHandler;

    public DialogFragmentWarningSubscription(@NonNull String tag, @NonNull String message, @NonNull Consumer<DialogFragmentWarning.Answer> onCancelHandler) {
        super(tag, message);
        this.onCancelHandler = onCancelHandler;
    }

}
