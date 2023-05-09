package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class UniqueObservable extends ExtendedObservable {

    protected Double coefficient;

    public UniqueObservable(Integer id,
                            Double coefficient,
                            Timestamp createdAt,
                            Timestamp updatedAt) {

        super(id, createdAt, updatedAt);

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
