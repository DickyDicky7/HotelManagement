package com.example.common;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapters.ExtendedAdapter;
import com.example.hotelmanagement.observables.ExtendedObservable;
import com.example.search.SearchProcessor;
import com.example.search.SearchStrategy;

import java.util.List;
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

}
