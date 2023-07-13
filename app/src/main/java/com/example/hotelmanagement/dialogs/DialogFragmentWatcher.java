package com.example.hotelmanagement.dialogs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.hotelmanagement.dialogs.subscriptions.DialogFragmentFailureSubscription;
import com.example.hotelmanagement.dialogs.subscriptions.DialogFragmentSubscription;
import com.example.hotelmanagement.dialogs.subscriptions.DialogFragmentSuccessSubscription;
import com.example.hotelmanagement.dialogs.subscriptions.DialogFragmentWarningSubscription;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DialogFragmentWatcher {

    @NonNull
    public static final AtomicBoolean destroyed = new AtomicBoolean(false);
    @NonNull
    public static final LinkedBlockingDeque<DialogFragmentSubscription> dialogFragmentSubscriptions = new LinkedBlockingDeque<>();
    @Nullable
    public static Thread watcher;

    public static void dialogFragmentSubscribe(@NonNull DialogFragmentSubscription dialogFragmentSubscription) {
        dialogFragmentSubscriptions.addLast(dialogFragmentSubscription);
    }

    public static void dialogFragmentSuccessSubscribe(@NonNull String tag, @NonNull String message) {
        dialogFragmentSubscribe(new DialogFragmentSuccessSubscription(tag, message));
    }

    public static void dialogFragmentFailureSubscribe(@NonNull String tag, @NonNull String message) {
        dialogFragmentSubscribe(new DialogFragmentFailureSubscription(tag, message));
    }

    public static void dialogFragmentWarningSubscribe(@NonNull String tag, @NonNull String message, @NonNull Consumer<DialogFragmentWarning.Answer> onCancelHandler) {
        dialogFragmentSubscribe(new DialogFragmentWarningSubscription(tag, message, onCancelHandler));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static void processDialogFragmentSubscriptions(@NonNull FragmentManager fragmentManager) {
        watcher = new Thread(() -> {
            while (!destroyed.get()) {
                //Log.d("Watcher Thread Id", String.valueOf(Thread.currentThread().getId()));
                if (dialogFragmentSubscriptions.isEmpty()) {
                } else {
                    DialogFragmentSubscription dialogFragmentSubscription = dialogFragmentSubscriptions.getFirst();
                    dialogFragmentSubscriptions.removeFirst();
                    if (dialogFragmentSubscription instanceof DialogFragmentSuccessSubscription) {
                        DialogFragmentSuccessSubscription dialogFragmentSuccessSubscription = (DialogFragmentSuccessSubscription) dialogFragmentSubscription;
                        DialogFragmentSuccess.newOne(fragmentManager, dialogFragmentSuccessSubscription.tag, dialogFragmentSuccessSubscription.message);
                    } else if (dialogFragmentSubscription instanceof DialogFragmentFailureSubscription) {
                        DialogFragmentFailureSubscription dialogFragmentFailureSubscription = (DialogFragmentFailureSubscription) dialogFragmentSubscription;
                        DialogFragmentFailure.newOne(fragmentManager, dialogFragmentFailureSubscription.tag, dialogFragmentFailureSubscription.message);
                    } else if (dialogFragmentSubscription instanceof DialogFragmentWarningSubscription) {
                        DialogFragmentWarningSubscription dialogFragmentWarningSubscription = (DialogFragmentWarningSubscription) dialogFragmentSubscription;
                        DialogFragmentWarning.newOne(fragmentManager, dialogFragmentWarningSubscription.tag, dialogFragmentWarningSubscription.message, dialogFragmentWarningSubscription.onCancelHandler);
                    }
                }
            }
        });
        watcher.start();
    }

}
