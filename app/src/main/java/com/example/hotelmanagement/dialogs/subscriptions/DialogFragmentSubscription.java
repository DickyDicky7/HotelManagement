package com.example.hotelmanagement.dialogs.subscriptions;

import androidx.annotation.NonNull;

public class DialogFragmentSubscription {

    @NonNull
    public String tag;
    @NonNull
    public String message;

    public DialogFragmentSubscription(@NonNull String tag, @NonNull String message) {
        this.tag = tag;
        this.message = message;
    }

}

