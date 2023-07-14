package com.example.hotelmanagement.observable.implementation;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;
import com.example.hotelmanagement.observable.abstraction.ExtendedObservable;

import java.time.LocalDateTime;

public class GuestKindObservable extends ExtendedObservable {

    protected String name;
    protected Double discountCoefficient;

    public GuestKindObservable() {
        super();
    }

    public GuestKindObservable(Integer id,
                               String name,
                               Double discount_coefficient,
                               LocalDateTime created_at,
                               LocalDateTime updated_at) {

        super(id, created_at, updated_at);

        this.name = name;
        this.discountCoefficient = discount_coefficient;

    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @NonNull
    public Boolean customizedEquals(@NonNull GuestKindObservable guestKindObservable) throws IllegalAccessException {
        return ExtendedObservable.customizedEquals(this, guestKindObservable);
    }

    @NonNull
    public GuestKindObservable customizedClone() {
        return ExtendedObservable.customizedClone(this);
    }

}
