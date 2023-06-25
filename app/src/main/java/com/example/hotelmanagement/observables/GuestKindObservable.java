package com.example.hotelmanagement.observables;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.time.LocalDateTime;

public class GuestKindObservable extends ExtendedObservable {

    protected String name;

    public GuestKindObservable() {
        super();
    }

    public GuestKindObservable(Integer id,
                               String name,
                               LocalDateTime created_at,
                               LocalDateTime updated_at) {

        super(id, created_at, updated_at);

        this.name = name;

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
