package com.example.search;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public abstract class SearchStrategy<VM extends ViewModel> {

    @NonNull
    protected VM viewModel;

    public SearchStrategy(@NonNull VM viewModel) {
        this.viewModel = viewModel;
    }

    public abstract void processSearch(@NonNull String searchText);

}
