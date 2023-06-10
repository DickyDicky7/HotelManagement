package com.example.hotelmanagement.observables;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.time.LocalDateTime;

public class ExtendedObservable extends BaseObservable {

    protected Integer id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    public ExtendedObservable() {
        super();
    }

    public ExtendedObservable(Integer id,
                              LocalDateTime created_at,
                              LocalDateTime updated_at) {

        super();

        this.id = id;
        this.createdAt = created_at;
        this.updatedAt = updated_at;

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
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        notifyPropertyChanged(BR.createdAt);
    }

    @Bindable
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        notifyPropertyChanged(BR.updatedAt);
    }

}
