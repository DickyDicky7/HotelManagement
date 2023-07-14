package com.example.hotelmanagement.search.processor;

import android.os.Handler;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.CursorAdapter;

import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.search.strategy.abstraction.SearchStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchProcessor {

    @NonNull
    protected String LstSearchText = "";

    @NonNull
    protected SearchView searchView;
    @NonNull
    protected SearchStrategy<?> searchStrategy;
    @NonNull
    protected AutoCompleteTextView searchAutoCompleteTextView;

    @NonNull
    protected Handler onSearchHandler;
    @NonNull
    protected Runnable onSearchCallback;
    @NonNull
    protected Consumer<?> onSearchConsumer;

    public <SearchResult> SearchProcessor(@NonNull SearchView searchView,
                                          @NonNull SearchStrategy<?> searchStrategy,
                                          @NonNull Consumer<List<SearchResult>> onSearchConsumer) {

        this.searchView = searchView;
        this.searchStrategy = searchStrategy;
        this.searchAutoCompleteTextView = searchView.findViewById
                (androidx.appcompat.R.id.search_src_text);

        this.onSearchHandler = new Handler();
        this.onSearchConsumer = onSearchConsumer;
        this.onSearchCallback = () -> {
            checkToShowSuggestionsOrNot
                    (searchView.getQuery().toString());
            onSearchConsumer.accept(this.processSearch
                    (searchView.getQuery().toString()));
        };

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Common.searchViewOnFocusChangeForwardingHandler = null;
        searchAutoCompleteTextView.dismissDropDown();
        onSearchHandler.removeCallbacks(onSearchCallback);
    }

    public void start() {

        searchAutoCompleteTextView.setThreshold(100);
        searchAutoCompleteTextView.setDropDownVerticalOffset(10);

        int delayMilliseconds = 500;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newSearchText) {

                onSearchHandler.removeCallbacks(onSearchCallback);
                onSearchCallback.run();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newSearchText) {

                checkToShowSuggestionsOrNot(newSearchText, LstSearchText);
                LstSearchText = newSearchText;
                onSearchHandler.removeCallbacks(onSearchCallback);
                onSearchHandler.postDelayed(onSearchCallback, delayMilliseconds);

                return false;
            }
        });

        Common.searchViewOnFocusChangeForwardingHandler = isFocused -> {
            if (isFocused) {
                checkToShowSuggestionsOrNot(searchView.getQuery().toString());
            }
        };

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                if (searchStrategy.getSuggestion(position).equals("")) {
                    String searchText = searchView.getQuery().toString();
                    String SearchText = "$$$";
                    boolean submitted = false;
                    searchView.setQuery(SearchText, submitted);
                    searchView.setQuery(searchText, submitted);
                } else {
                    String searchText = searchView.getQuery().toString() + (searchView
                            .getQuery().toString().endsWith(",") ? " " : "") + searchStrategy.getSuggestion(position);
                    boolean submitted = false;
                    searchView.setQuery(searchText, submitted);
                    int blankedLength = 0;
                    Matcher matcher = Pattern.compile("(<.*>)").matcher(searchStrategy.getSuggestion(position));
                    if (matcher.find()) {
                        String matchedText = matcher.group(0);
                        if (null != matchedText) {
                            blankedLength = matchedText.length();
                        }
                    }
                    int LstIndex = searchText.length();
                    int FstIndex = LstIndex - blankedLength;
                    searchAutoCompleteTextView.setSelection(FstIndex, LstIndex);
                }
                return true;
            }
        });

    }

    public void showSuggestions(@NonNull String searchText) {
        CursorAdapter suggestionsAdapter = searchStrategy.getSuggestionsAdapter(searchText);
        searchView.setSuggestionsAdapter(suggestionsAdapter);
        searchAutoCompleteTextView.setThreshold(0);
        searchAutoCompleteTextView.showDropDown();
    }

    public void hideSuggestions(@NonNull String searchText) {
        searchAutoCompleteTextView.setThreshold(100);
        searchAutoCompleteTextView.dismissDropDown();
    }

    public void checkToShowSuggestionsOrNot(
            @NonNull String newSearchText,
            @NonNull String LstSearchText) {
        if ((newSearchText.matches("\\s*") || newSearchText.trim().endsWith(",")) && newSearchText.length() >= LstSearchText.length()) {
            showSuggestions(newSearchText);
        } else {
            hideSuggestions(newSearchText);
        }
    }

    public void checkToShowSuggestionsOrNot(
            @NonNull String currentSearchText) {
        if (currentSearchText.matches("\\s*") || currentSearchText.trim().endsWith(",")) {
            showSuggestions(currentSearchText);
        } else {
            hideSuggestions(currentSearchText);
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <SearchResult> List<SearchResult> processSearch(@NonNull String searchText) {
        try {
            return (List<SearchResult>) searchStrategy.processSearch(searchText);
        } catch (ClassCastException e) {
            return new ArrayList<>();
        }
    }

    public void replaceSearchView(@NonNull SearchView searchView) {
        this.searchView = searchView;
    }

    public void replaceSearchStrategy(@NonNull SearchStrategy<?> searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public <SearchResult> void replaceOnSearchConsumer(@NonNull Consumer<List<SearchResult>> onSearchConsumer) {
        this.onSearchConsumer = onSearchConsumer;
    }

}
