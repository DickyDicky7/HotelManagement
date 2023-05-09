package com.example.hotelmanagement.adapters_viewmodels;

import android.annotation.SuppressLint;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Vector;

public abstract class ExtendedAdapterViewModel<O extends BaseObservable, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<V> {

    protected final MutableLiveData<List<O>> modelState;

    @SuppressLint("NotifyDataSetChanged")
    public ExtendedAdapterViewModel() {
        super();
        modelState = new MutableLiveData<List<O>>(new Vector<O>());
        modelState.observeForever(observables -> notifyDataSetChanged());
    }

    public LiveData<List<O>> getModelState() {
        return modelState;
    }

    public void Insert(@NonNull O observable) {
        List<O> observables = modelState.getValue();
        if (observables != null) {
            observables.add(observable);
            modelState.setValue(observables);
        }
    }

    public void Delete(@NonNull O observable) {
        List<O> observables = modelState.getValue();
        if (observables != null && observables.contains(observable)) {
            observables.remove(observable);
            modelState.setValue(observables);
        }
    }

    public void Update(@NonNull O observable) {
        List<O> observables = modelState.getValue();
        if (observables != null && observables.contains(observable)) {
            observables.set(observables.indexOf(observable), observable);
            modelState.setValue(observables);
        }
    }

    public void Update(@NonNull List<O> observables) {
        modelState.setValue(observables);
    }

    @NonNull
    @Override
    public abstract V onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull V holder, int position);

    @Override
    public int getItemCount() {
        List<O> observables = modelState.getValue();
        return observables != null ? observables.size() : 0;
    }

}
