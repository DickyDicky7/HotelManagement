package com.example.hotelmanagement.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.LinkedList;
import java.util.List;

public abstract class ExtendedViewModel<BO extends BaseObservable> extends ViewModel {

    public Boolean dataLoaded;
    protected final MutableLiveData<List<BO>> modelState;

    public ExtendedViewModel() {
        super();
        dataLoaded = false;
        modelState = new MutableLiveData<List<BO>>(new LinkedList<BO>());
    }

    public LiveData<List<BO>> getModelState() {
        return modelState;
    }

    public static <BO extends BaseObservable, VM extends ExtendedViewModel<BO>>
    VM getViewModel(FragmentActivity fragmentActivity, Class<VM> viewModelClass) {
        VM viewModel = new ViewModelProvider(fragmentActivity).get(viewModelClass);
        if (!viewModel.dataLoaded) viewModel.loadData();
        return viewModel;
    }

    public abstract void loadData();

}
