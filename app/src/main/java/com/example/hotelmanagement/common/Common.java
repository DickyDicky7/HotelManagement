package com.example.hotelmanagement.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;

import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.exception.ApolloException;
import com.cloudinary.android.callback.ErrorInfo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.abstraction.ExtendedAdapter;
import com.example.hotelmanagement.dialog.implementation.DialogFragmentWarning;
import com.example.hotelmanagement.dialog.watcher.DialogFragmentWatcher;
import com.example.hotelmanagement.observables.ExtendedObservable;
import com.example.hotelmanagement.popupwindow.implementation.PopupWindowLoading;
import com.example.hotelmanagement.search.processor.SearchProcessor;
import com.example.hotelmanagement.search.strategy.abstraction.SearchStrategy;
import com.example.hotelmanagement.viewmodel.abstraction.ExtendedViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class Common {

    @Nullable
    public static Consumer<Boolean> searchViewOnFocusChangeForwardingHandler = null;

    public static void beautifySearchView(@NonNull SearchView searchView, @NonNull Context context) {

        searchView.setIconifiedByDefault(false);
        searchView.setQuery(null, false);

        EditText editText = searchView.findViewById
                (androidx.appcompat.R.id.search_src_text);
        ImageView searchIcons = searchView.findViewById
                (androidx.appcompat.R.id.search_mag_icon);
        ImageView closeButton = searchView.findViewById
                (androidx.appcompat.R.id.search_close_btn);

        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editText.setTextColor(Color.GRAY);
        editText.setCursorVisible(true);
        editText.setHintTextColor(Color.GRAY);
        searchIcons.setColorFilter(Color.GRAY);
        closeButton.setColorFilter(Color.GRAY);

        editText.setOnFocusChangeListener((_view_, isFocused) -> {

            if (isFocused) {

                editText.setTextColor(context.getColor(R.color.indigo_400));
                editText.setHintTextColor(context.getColor(R.color.indigo_400));
                searchIcons.setColorFilter(context.getColor(R.color.indigo_400));
                closeButton.setColorFilter(context.getColor(R.color.indigo_400));

            } else {

                editText.setTextColor(Color.GRAY);
                editText.setHintTextColor(Color.GRAY);
                searchIcons.setColorFilter(Color.GRAY);
                closeButton.setColorFilter(Color.GRAY);

            }

            if (searchViewOnFocusChangeForwardingHandler != null) {
                searchViewOnFocusChangeForwardingHandler.accept(isFocused);
            }

        });

    }

    @SuppressWarnings("ParameterCanBeLocal")
    public static <SearchResult extends ExtendedObservable>
    void setupSearchFeatureInListLikeFragment(
            @Nullable SearchProcessor searchProcessor,
            @NonNull SearchView searchView,
            @NonNull SearchStrategy<SearchResult> searchStrategy,
            @Nullable Consumer<List<SearchResult>> onSearchConsumer, @NonNull ExtendedAdapter<SearchResult, ?> extendedAdapter
    ) {

        onSearchConsumer = extendedObservables -> {

            if (extendedAdapter.HasTheSame(extendedObservables)) {
                if (searchView.getQuery().toString().equals("")) {
                    extendedAdapter.Clear();
                    extendedAdapter.Fill(extendedObservables);
                }
                return;
            }

            extendedAdapter.Clear();
            extendedAdapter.Fill(extendedObservables);

        };

        searchProcessor = new SearchProcessor(searchView, searchStrategy, onSearchConsumer);

        searchProcessor.start();

    }

    public static void hideKeyboard(@NonNull FragmentActivity fragmentActivity) {
        View currentFocusView = fragmentActivity.getCurrentFocus();
        if (null != currentFocusView) {
            InputMethodManager inputMethodManager = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            int flags = 0;
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.getWindowToken(), flags);
        }
    }

    public static void showCustomSnackBar(@NonNull String message, @NonNull Context context, @NonNull View snackBarAttachView) {
        Snackbar snackbar = Snackbar.make(snackBarAttachView, message, Snackbar.LENGTH_SHORT);
        TextView textView = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.outfit));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setTextColor(context.getColor(R.color.white_100));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        snackbar.getView().getBackground().setColorFilter(context.getColor(R.color.red_300), PorterDuff.Mode.SRC_IN);
        snackbar.show();
    }

    @SuppressWarnings({"DuplicateBranchesInSwitch", "RedundantSuppression"})
    @NonNull
    public static String getSuccessMessage(Action action) {
        String successMessage;
        switch (action) {
            case INSERT_ITEM: {
                successMessage = "Success: Your item has been inserted successfully.";
            }
            break;
            case UPDATE_ITEM: {
                successMessage = "Success: Your item has been updated successfully.";
            }
            break;
            case DELETE_ITEM: {
                successMessage = "Success: Your item has been deleted successfully.";
            }
            break;
            default: {
                successMessage = "\uD83D\uDC4D";
            }
            break;
        }
        return successMessage;
    }

    @SuppressWarnings({"DuplicateBranchesInSwitch", "RedundantSuppression"})
    @NonNull
    public static String getWarningMessage(Action action) {
        String warningMessage;
        switch (action) {
            case INSERT_ITEM: {
                warningMessage = "✋";
            }
            break;
            case UPDATE_ITEM: {
                warningMessage = "✋";
            }
            break;
            case DELETE_ITEM: {
                warningMessage = "Caution: Deleting this item will result in permanent removal from the system.";
            }
            break;
            default: {
                warningMessage = "✋";
            }
            break;
        }
        return warningMessage;
    }

    @NonNull
    public static String getFailureMessage(@Nullable List<Error> apolloErrors, @Nullable ApolloException apolloException, @Nullable ErrorInfo cloudinaryErrorInfo) {
        return apolloErrors != null ? apolloErrors.get(0).getMessage() : apolloException != null ? apolloException.toString() : cloudinaryErrorInfo != null ? cloudinaryErrorInfo.toString() : "Failure: Something wrong happened.";
    }

    public static <EO extends ExtendedObservable> void onDeleteRecyclerViewItemClickHandler(
            @NonNull ExtendedViewModel<EO> extendedViewModel,
            @NonNull EO extendedObservable,
            @NonNull String warningTag,
            @NonNull String successTag,
            @NonNull String failureTag,
            @NonNull ViewGroup _parent,
            @NonNull FragmentActivity fragmentActivity, final @NonNull AtomicBoolean dismissPopupWindowLoading) {

        Common.hideKeyboard(fragmentActivity);
        Consumer<DialogFragmentWarning.Answer> onCancelHandler = answer -> {
            if (answer == DialogFragmentWarning.Answer.YES) {
                PopupWindowLoading popupWindowLoading = PopupWindowLoading.newOne(fragmentActivity.getLayoutInflater(), _parent);
                popupWindowLoading.showAsDropDown(_parent);
                new Thread(() -> {
                    String tag = "PopupWindowLoading";
                    String msg = "On Screen";
                    while (!dismissPopupWindowLoading.get()) {
                        Log.d(tag, msg);
                    }
                    dismissPopupWindowLoading.set(false);
                    fragmentActivity.runOnUiThread(popupWindowLoading::dismiss);
                }).start();
                extendedViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    dismissPopupWindowLoading.set(true);
                    DialogFragmentWatcher.dialogFragmentFailureSubscribe(failureTag, Common.getFailureMessage
                            (apolloErrors, apolloException, cloudinaryErrorInfo));
                };
                extendedViewModel.onSuccessCallback = () -> {
                    dismissPopupWindowLoading.set(true);
                    DialogFragmentWatcher.dialogFragmentSuccessSubscribe(successTag, Common.getSuccessMessage
                            (Action.DELETE_ITEM));
                };
                extendedViewModel.onFailureCallback = null;
                extendedViewModel.delete(extendedObservable);
            }
        };
        DialogFragmentWatcher.dialogFragmentWarningSubscribe(warningTag, Common.getWarningMessage
                (Action.DELETE_ITEM), onCancelHandler);

    }

    public static <EO extends ExtendedObservable> void onButtonDoneFragmentEdtClickHandler(
            @Nullable Consumer<EO> beforeUpdatePrepareUsedExtendedObservableConsumer,
            @NonNull ExtendedViewModel<EO> extendedViewModel,
            @NonNull EO usedExtendedObservable,
            @NonNull EO copyExtendedObservable,
            @NonNull String successTag,
            @NonNull String failureTag,
            @NonNull ViewGroup _parent,
            @NonNull NavController navController,
            @NonNull FragmentActivity fragmentActivity,
            final @NonNull AtomicBoolean dismissPopupWindowLoading,
            final @NonNull AtomicBoolean alreadyPoppedBackStackNow, @NonNull String... excludedFields) {

        try {
            extendedViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                dismissPopupWindowLoading.set(true);
                DialogFragmentWatcher.dialogFragmentFailureSubscribe(failureTag, Common.getFailureMessage(
                        apolloErrors, apolloException, cloudinaryErrorInfo));
            };
            extendedViewModel.onSuccessCallback = () -> {
                dismissPopupWindowLoading.set(true);
                DialogFragmentWatcher.dialogFragmentSuccessSubscribe(successTag, Common.getSuccessMessage(
                        Common.Action.UPDATE_ITEM));
                try {
                    int milliseconds = 1000;
                    Thread.sleep(milliseconds);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!alreadyPoppedBackStackNow.get()) {
                    fragmentActivity.runOnUiThread(navController::popBackStack);
                }
            };
            extendedViewModel.onFailureCallback = null;
            if (extendedViewModel.checkObservable(usedExtendedObservable, fragmentActivity, _parent, excludedFields)) {
                if (beforeUpdatePrepareUsedExtendedObservableConsumer != null) {
                    beforeUpdatePrepareUsedExtendedObservableConsumer.accept(usedExtendedObservable);
                }
                extendedViewModel.update(usedExtendedObservable, copyExtendedObservable);
                PopupWindowLoading popupWindowLoading = PopupWindowLoading.newOne(fragmentActivity.getLayoutInflater(), _parent);
                popupWindowLoading.showAsDropDown(_parent);
                new Thread(() -> {
                    String tag = "PopupWindowLoading";
                    String msg = "On Screen";
                    while (!dismissPopupWindowLoading.get()) {
                        Log.d(tag, msg);
                    }
                    dismissPopupWindowLoading.set(false);
                    fragmentActivity.runOnUiThread(popupWindowLoading::dismiss);
                }).start();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Common.hideKeyboard(fragmentActivity);

    }

    public enum Action {
        INSERT_ITEM,
        UPDATE_ITEM,
        DELETE_ITEM,
    }

}
