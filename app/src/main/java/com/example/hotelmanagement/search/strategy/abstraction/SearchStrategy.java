package com.example.hotelmanagement.search.strategy.abstraction;

import android.app.SearchManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.example.hotelmanagement.R;

import java.util.ArrayList;
import java.util.List;

public abstract class SearchStrategy<SearchResult> {

    @NonNull
    protected CursorAdapter cursorAdapter;

    @NonNull
    protected List<String> suggestionKs;
    @NonNull
    protected List<String> suggestionVs;

    public SearchStrategy(@NonNull Context context) {
        this.suggestionKs = new ArrayList<>();
        this.suggestionVs = new ArrayList<>();
        this.cursorAdapter = new SimpleCursorAdapter(context, R.layout.item_search, null, new String[
                ]{SearchManager.SUGGEST_COLUMN_TEXT_1}, new int[]{R.id.item_search_text_view}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @NonNull
    public abstract List<SearchResult> processSearch(@NonNull String searchText);

    @NonNull
    public String getSuggestion(@NonNull Integer position) {
        try {
            return suggestionVs.get(position);
        } catch (Exception e) {
            return "";
        }
    }

    @NonNull
    public abstract CursorAdapter getSuggestionsAdapter(@NonNull String searchText);

}
