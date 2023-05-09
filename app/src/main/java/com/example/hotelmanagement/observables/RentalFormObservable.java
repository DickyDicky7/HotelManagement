package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class RentalFormObservable extends ExtendedObservable {

    protected Integer roomId;
    protected Integer billId;
    protected BigDecimal amount;
    protected Integer rentalDays;
    protected Boolean isResolved;
    protected BigDecimal pricePerDay;
    protected LocalDateTime startDate;

    public RentalFormObservable(Integer id,
                                Integer roomId,
                                Integer billId,
                                BigDecimal amount,
                                Integer rentalDays,
                                Boolean isResolved,
                                Timestamp createdAt,
                                Timestamp updatedAt,
                                BigDecimal pricePerDay,
                                LocalDateTime startDate) {

        super(id, createdAt, updatedAt);

        this.roomId = roomId;
        this.billId = billId;
        this.amount = amount;
        this.startDate = startDate;
        this.rentalDays = rentalDays;
        this.isResolved = isResolved;
        this.pricePerDay = pricePerDay;

    }

    @Bindable
    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
        notifyPropertyChanged(BR.roomId);
    }

    @Bindable
    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
        notifyPropertyChanged(BR.billId);
    }

    @Bindable
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
        notifyPropertyChanged(BR.rentalDays);
    }

    @Bindable
    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
        notifyPropertyChanged(BR.isResolved);
    }

    @Bindable
    public BigDecimal getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(BigDecimal pricePerDay) {
        this.pricePerDay = pricePerDay;
        notifyPropertyChanged(BR.pricePerDay);
    }

    @Bindable
    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        notifyPropertyChanged(BR.startDate);
    }

}
