package com.example.hotelmanagement.observables;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class ExtendedObservable extends BaseObservable {

    protected Integer id;
    protected Timestamp createdAt;
    protected Timestamp updatedAt;

    public ExtendedObservable() {

    }

    public ExtendedObservable(Integer id,
                              Timestamp createdAt,
                              Timestamp updatedAt) {

        super();

        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    @Bindable
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        notifyPropertyChanged(BR.createdAt);
    }

    @Bindable
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
        notifyPropertyChanged(BR.updatedAt);
    }

}
