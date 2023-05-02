package com.example.hotelmanagement.observable;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BillObservable extends ExtendedObservable {

    protected Boolean isPaid;
    protected Integer guestId;
    protected BigDecimal cost;

    public BillObservable(Integer id,
                          Boolean isPaid,
                          Integer guestId,
                          BigDecimal cost,
                          Timestamp createdAt,
                          Timestamp updatedAt) {

        super(id, createdAt, updatedAt);

        this.cost = cost;
        this.isPaid = isPaid;
        this.guestId = guestId;

    }

    @Bindable
    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
        notifyPropertyChanged(BR.isPaid);
    }

    @Bindable
    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
        notifyPropertyChanged(BR.guestId);
    }

    @Bindable
    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
        notifyPropertyChanged(BR.cost);
    }

}
