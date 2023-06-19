package com.example.hotelmanagement.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public abstract class ExtendedViewModel<BO extends BaseObservable> extends ViewModel {

    protected final MutableLiveData<List<BO>> modelState;
    protected final List<Runnable> subscriptionObservers;
    public Runnable onSuccessCallback;
    public Runnable onFailureCallback;

    public ExtendedViewModel() {
        super();
        subscriptionObservers = new LinkedList<Runnable>();
        modelState = new MutableLiveData<List<BO>>(new LinkedList<BO>());
    }

    public abstract void startSubscription();

    public LiveData<List<BO>> getModelState() {
        return modelState;
    }

    public void fillSubscriptionObservers(List<Runnable> newSubscriptionObservers) {
        subscriptionObservers.addAll(newSubscriptionObservers);
    }

    public void clearSubscriptionObservers() {
        subscriptionObservers.clear();
    }

    public void insertSubscriptionObserver(Runnable subscriptionObserver) {
        subscriptionObservers.add(subscriptionObserver);
    }

    public void removeSubscriptionObserver(Runnable subscriptionObserver) {
        subscriptionObservers.remove(subscriptionObserver);
    }

    public void notifySubscriptionObservers() {
        subscriptionObservers.removeIf(Objects::isNull);
        subscriptionObservers.forEach(Runnable::run);
    }

    public void repostModelState() {
        modelState.postValue(modelState.getValue());
    }

    public void resetModelState() {
        modelState.setValue(modelState.getValue());
    }

    public Boolean checkObservable(BO baseObservable, Context context, String... excludedFields) throws IllegalAccessException {
        for (Field field : baseObservable.getClass().getDeclaredFields()) {
            if (Arrays.stream(excludedFields).noneMatch(excludedField -> excludedField.equals(field.getName()))) {
                field.setAccessible(true);
                if (field.get(baseObservable) == null || field.get(baseObservable).toString().equals("")) {
                    Toast.makeText(context, ("missing " + Arrays.stream(field.getName().split
                            ("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")).reduce
                            ("", (sentence, word) -> sentence + " " + word))
                            .toUpperCase(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

}
