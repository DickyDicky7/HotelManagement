package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class BillObservable extends ExtendedObservable {

    protected String name;
    protected Double cost;
    protected Boolean isPaid;
    protected String idNumber;
    protected Integer guestId;

    public BillObservable() {
        super();
    }

    public BillObservable(Integer id,
                          Double cost,
                          Boolean isPaid,
                          Integer guestId,
                          Timestamp createdAt,
                          Timestamp updatedAt) {

        super(id, createdAt, updatedAt);

        this.cost = cost;
        this.isPaid = isPaid;
        this.guestId = guestId;

    }

    @Bindable
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
        notifyPropertyChanged(BR.idNumber);
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
    public String getGuestIdString() {
        try {
            return guestId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setGuestIdString(String guestIdString) {
        try {
            setGuestId(Integer.valueOf(guestIdString));
            notifyPropertyChanged(BR.guestIdString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
        notifyPropertyChanged(BR.cost);
    }

    @Bindable
    public String getCostString() {
        try {
            return cost.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setCostString(String costString) {
        try {
            setCost(Double.valueOf(costString));
            notifyPropertyChanged(BR.costString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
