package com.example.search;

import androidx.annotation.NonNull;

public class SearchProcessor {

    @NonNull
    private SearchStrategy searchStrategy;

    public SearchProcessor(@NonNull SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public void processSearch(@NonNull String searchText) {
        searchStrategy.processSearch(searchText);
    }

    public void replaceSearchStrategy(@NonNull SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

}
