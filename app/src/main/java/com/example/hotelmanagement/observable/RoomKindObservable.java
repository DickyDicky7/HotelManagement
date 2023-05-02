package com.example.hotelmanagement.observable;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RoomKindObservable extends ExtendedObservable {

    protected String name;
    protected BigDecimal price;
    protected Integer capacity;
    protected Double surchargePercentage;
    protected Integer surchargeStartOrdinal;

    public RoomKindObservable(Integer id,
                              String name,
                              BigDecimal price,
                              Integer capacity,
                              Timestamp createdAt,
                              Timestamp updatedAt,
                              Double surchargePercentage,
                              Integer surchargeStartOrdinal) {

        super(id, createdAt, updatedAt);

        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.surchargePercentage = surchargePercentage;
        this.surchargeStartOrdinal = surchargeStartOrdinal;

    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        notifyPropertyChanged(BR.capacity);
    }

    @Bindable
    public Double getSurchargePercentage() {
        return surchargePercentage;
    }

    public void setSurchargePercentage(Double surchargePercentage) {
        this.surchargePercentage = surchargePercentage;
        notifyPropertyChanged(BR.surchargePercentage);
    }

    @Bindable
    public Integer getSurchargeStartOrdinal() {
        return surchargeStartOrdinal;
    }

    public void setSurchargeStartOrdinal(Integer surchargeStartOrdinal) {
        this.surchargeStartOrdinal = surchargeStartOrdinal;
        notifyPropertyChanged(BR.surchargeStartOrdinal);
    }

}
