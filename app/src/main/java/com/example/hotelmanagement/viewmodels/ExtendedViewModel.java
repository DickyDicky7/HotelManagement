package com.example.hotelmanagement.viewmodels;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.LinkedList;
import java.util.List;

public class ExtendedViewModel<BO extends BaseObservable> extends ViewModel {

    protected final MutableLiveData<List<BO>> modelState;

    public ExtendedViewModel() {
        super();
        modelState = new MutableLiveData<List<BO>>(new LinkedList<BO>());
    }

    public LiveData<List<BO>> getModelState() {
        return modelState;
    }

}
