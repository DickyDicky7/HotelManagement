package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class BillObservable extends ExtendedObservable {
    protected String idNumber;
    protected String name;
    protected Boolean isPaid;
    protected Integer guestId;
    protected Double cost;

    public BillObservable() {
        super();
    }

    public BillObservable(Integer id,               //auto generate
                          Boolean isPaid,           //input
                          Integer guestId,          //find by id number
                          Double cost,              //auto calculate
                          Timestamp createdAt,      //auto generate
                          Timestamp updatedAt)      //auto generate
    {

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
        notifyPropertyChanged(BR.guestIdString);
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
        notifyPropertyChanged(BR.costString);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
