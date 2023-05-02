package com.example.hotelmanagement.observable;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class RentalFormGuestObservable extends ExtendedObservable {

    protected Integer guestId;
    protected Integer rentalFormId;

    public RentalFormGuestObservable(Integer guestId,
                                     Timestamp createdAt,
                                     Timestamp updatedAt,
                                     Integer rentalFormId) {

        super(null, createdAt, updatedAt);

        this.guestId = guestId;
        this.rentalFormId = rentalFormId;

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
    public Integer getRentalFormId() {
        return rentalFormId;
    }

    public void setRentalFormId(Integer rentalFormId) {
        this.rentalFormId = rentalFormId;
        notifyPropertyChanged(BR.rentalFormId);
    }

}
