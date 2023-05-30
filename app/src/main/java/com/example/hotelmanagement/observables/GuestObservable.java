package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class GuestObservable extends ExtendedObservable {

    protected String name;
    protected String address;
    protected String idNumber;
    protected Integer guestKindId;
    protected String phoneNumber;

    public GuestObservable(Integer id,
                           String name,
                           String address,
                           String idNumber,
                           Integer guestKindId,
                           String phoneNumber,
                           Timestamp createdAt,
                           Timestamp updatedAt) {

        super(id, createdAt, updatedAt);

        this.name = name;
        this.address = address;
        this.idNumber = idNumber;
        this.guestKindId = guestKindId;
        this.phoneNumber = phoneNumber;
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
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
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
    public Integer getGuestKindId() {
        return guestKindId;
    }

    public void setGuestKindId(Integer guestKindId) {
        this.guestKindId = guestKindId;
        notifyPropertyChanged(BR.guestKindId);
    }
    @Bindable
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber=phoneNumber;
    }
}
