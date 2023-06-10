package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.time.LocalDateTime;

public class GuestObservable extends ExtendedObservable {

    protected String name;
    protected String address;
    protected String idNumber;
    protected String phoneNumber;
    protected Integer guestKindId;

    public GuestObservable() {
        super();
    }

    public GuestObservable(Integer id,
                           String name,
                           String address,
                           String id_number,
                           String phone_number,
                           Integer guestkind_id,
                           LocalDateTime created_at,
                           LocalDateTime updated_at) {

        super(id, created_at, updated_at);

        this.name = name;
        this.address = address;
        this.idNumber = id_number;
        this.guestKindId = guestkind_id;
        this.phoneNumber = phone_number;

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
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

}
