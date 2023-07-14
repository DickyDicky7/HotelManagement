package com.example.hotelmanagement.dialog.subscription.implementation;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.dialog.subscription.abstraction.DialogFragmentSubscription;

public class DialogFragmentFailureSubscription extends DialogFragmentSubscription {

    public DialogFragmentFailureSubscription(@NonNull String tag, @NonNull String message) {
        super(tag, message);
    }

}
