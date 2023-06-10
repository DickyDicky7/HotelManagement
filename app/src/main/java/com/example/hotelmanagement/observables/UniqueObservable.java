package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.time.LocalDateTime;

public class UniqueObservable extends ExtendedObservable {

    protected Double coefficient;

    public UniqueObservable(Integer id,
                            Double coefficient,
                            LocalDateTime created_at,
                            LocalDateTime updated_at) {

        super(id, created_at, updated_at);

        this.coefficient = coefficient;

    }

    @Bindable
    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
        notifyPropertyChanged(BR.coefficient);
    }

}
