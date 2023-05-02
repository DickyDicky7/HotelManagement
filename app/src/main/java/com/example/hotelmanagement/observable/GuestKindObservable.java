package com.example.hotelmanagement.observable;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class GuestKindObservable extends ExtendedObservable {

    protected String name;

    public GuestKindObservable(Integer id,
                               String name,
                               Timestamp createdAt,
                               Timestamp updatedAt) {

        super(id, createdAt, updatedAt);

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

}
