package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Date;
import java.sql.Timestamp;


public class RentalFormObservable extends ExtendedObservable {
    protected String idNumber;
    protected String name;
    protected Integer guestId;
    protected Integer roomId;
    protected Integer billId;
    protected Integer numOfGuests;
    protected Double amount;
    protected Integer rentalDays;
    protected Boolean isResolved;
    protected Double pricePerDay;
    protected Date startDate;

    public RentalFormObservable() {
        super();
    }

    public RentalFormObservable(Integer id,                     //auto generate
                                Integer guestId,                //find by id number
                                Integer roomId,                 //input
                                Integer billId,                //default null
                                Double amount,                  //auto calculate
                                Integer rentalDays,             //input
                                Integer numOfGuests,            //input
                                Boolean isResolved,             //default false
                                Double pricePerDay,             //find by room id
                                Date startDate,                 //input
                                Timestamp createdAt,            //auto generate
                                Timestamp updatedAt) {          //auto generate

        super(id, createdAt, updatedAt);

        this.guestId = guestId;
        this.roomId = roomId;
        this.billId = billId;
        this.amount = amount;
        this.startDate = startDate;
        this.rentalDays = rentalDays;
        this.isResolved = isResolved;
        this.pricePerDay = pricePerDay;
        this.numOfGuests = numOfGuests;
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
    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
        notifyPropertyChanged(BR.idNumber);
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
    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
        notifyPropertyChanged(BR.roomIdString);
    }

    @Bindable
    public String getRoomIdString() {
        try {
            return roomId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setRoomIdString(String roomIdString) {
        try {
            setRoomId(Integer.valueOf(roomIdString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
        notifyPropertyChanged(BR.billIdString);
    }

    @Bindable
    public String getBillIdString() {
        try {
            return billId.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setBillILdString(String billILdString) {
        try {
            setBillId(Integer.valueOf(billILdString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amountString);
    }

    @Bindable
    public String getAmountString() {
        try {
            return amount.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setAmountString(String amountString) {
        try {
            setAmount(Double.valueOf(amountString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
        notifyPropertyChanged(BR.rentalDaysString);
    }

    @Bindable
    public String getRentalDaysString() {
        try {
            return rentalDays.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setRentalDaysString(String rentalDaysString) {
        try {
            setRentalDays(Integer.valueOf(rentalDaysString));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
        notifyPropertyChanged(BR.pricePerDayString);
    }

    @Bindable
    public String getPricePerDayString() {
        try {
            return pricePerDay.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setPricePerDayString(String pricePerDayString) {
        try {
            setPricePerDay(Double.valueOf(pricePerDayString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        notifyPropertyChanged(BR.startDateString);
    }

    @Bindable
    public String getStartDateString() {
        try {
            return startDate.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setStartDateString(String startDateString) {
        try {
            setStartDate(Date.valueOf(startDateString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public Integer getNumOfGuests() {
        return numOfGuests;
    }

    public void setNumOfGuests(Integer numOfGuests) {
        this.numOfGuests = numOfGuests;
        notifyPropertyChanged(BR.numOfGuests);
    }

    @Bindable
    public String getNumOfGuestsString() {
        try {
            return numOfGuests.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setNumOfGuestsString(String numOfGuestsString) {
        try {
            setNumOfGuests(Integer.valueOf(numOfGuestsString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
