package com.example.hotelmanagement.observables;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

public class ExtendedObservable extends BaseObservable {

    protected Integer id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected static final Gson gson = new Gson();

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

    @NonNull
    public static <EO extends ExtendedObservable> Boolean customizedEquals(@NonNull EO extendedObservable1, @NonNull EO extendedObservable2) throws IllegalAccessException {
        for (Field field : extendedObservable1.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            Object value1 = field.get(extendedObservable1);
            Object value2 = field.get(extendedObservable2);
            if (value1 == null && value2 == null) {
                continue;
            }
            if (value1 == null && value2 != null) {
                return false;
            }
            if (value1 != null && value2 == null) {
                return false;
            }
            if (!value1.equals(value2)) {
                return false;
            }
        }
        return true;
    }

    @NonNull
    public static <EO extends ExtendedObservable> EO customizedClone(@NonNull EO extendedObservable) {
        return (EO) gson.fromJson(gson.toJson(extendedObservable), extendedObservable.getClass());
    }

}
