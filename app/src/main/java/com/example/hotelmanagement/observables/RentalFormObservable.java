package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Date;
import java.sql.Timestamp;

public class RentalFormObservable extends ExtendedObservable {

    protected String name;
    protected Double amount;
    protected Integer roomId;
    protected Integer billId;
    protected Date startDate;
    protected String idNumber;
    protected Integer guestId;
    protected Integer rentalDays;
    protected Boolean isResolved;
    protected Double pricePerDay;
    protected Integer numOfGuests;

    public RentalFormObservable() {
        super();
    }

    public RentalFormObservable(Integer id,
                                Double amount,
                                Date startDate,
                                Integer roomId,
                                Integer billId,
                                Integer guestId,
                                Integer rentalDays,
                                Boolean isResolved,
                                Double pricePerDay,
                                Integer numOfGuests,
                                Timestamp createdAt,
                                Timestamp updatedAt) {

        super(id, createdAt, updatedAt);

        this.roomId = roomId;
        this.billId = billId;
        this.amount = amount;
        this.guestId = guestId;
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
        notifyPropertyChanged(BR.roomId);
    }

    @Bindable
    public String getRoomIdString() {
        try {
            return roomId.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setRoomIdString(String roomIdString) {
        try {
            setRoomId(Integer.valueOf(roomIdString));
        } catch (NullPointerException | NumberFormatException e) {
            setRoomId(null);
        } catch (Exception e) {
            setRoomId(null);
            e.printStackTrace();
        }
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
    public String getBillIdString() {
        try {
            return billId.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setBillIdString(String billIdString) {
        try {
            setBillId(Integer.valueOf(billIdString));
        } catch (NullPointerException | NumberFormatException e) {
            setBillId(null);
        } catch (Exception e) {
            setBillId(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public String getAmountString() {
        try {
            return amount.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setAmountString(String amountString) {
        try {
            setAmount(Double.valueOf(amountString));
        } catch (NullPointerException | NumberFormatException e) {
            setAmount(null);
        } catch (Exception e) {
            setAmount(null);
            e.printStackTrace();
        }
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
    public String getRentalDaysString() {
        try {
            return rentalDays.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setRentalDaysString(String rentalDaysString) {
        try {
            setRentalDays(Integer.valueOf(rentalDaysString));
        } catch (NullPointerException | NumberFormatException e) {
            setRentalDays(null);
        } catch (Exception e) {
            setRentalDays(null);
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
        notifyPropertyChanged(BR.pricePerDay);
    }

    @Bindable
    public String getPricePerDayString() {
        try {
            return pricePerDay.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setPricePerDayString(String pricePerDayString) {
        try {
            setPricePerDay(Double.valueOf(pricePerDayString));
        } catch (NullPointerException | NumberFormatException e) {
            setPricePerDay(null);
        } catch (Exception e) {
            setPricePerDay(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        notifyPropertyChanged(BR.startDate);
    }

    @Bindable
    public String getStartDateString() {
        try {
            return startDate.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setStartDateString(String startDateString) {
        try {
            setStartDate(Date.valueOf(startDateString));
        } catch (NullPointerException | IllegalArgumentException e) {
            setStartDate(null);
        } catch (Exception e) {
            setStartDate(null);
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
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setNumOfGuestsString(String numOfGuestsString) {
        try {
            setNumOfGuests(Integer.valueOf(numOfGuestsString));
        } catch (NullPointerException | NumberFormatException e) {
            setNumOfGuests(null);
        } catch (Exception e) {
            setNumOfGuests(null);
            e.printStackTrace();
        }
    }

}
