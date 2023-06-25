package com.example.hotelmanagement.observables;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RentalFormObservable extends ExtendedObservable {

    protected String name;
    protected Double amount;
    protected Integer roomId;
    protected Integer billId;
    protected String idNumber;
    protected Integer guestId;
    protected Integer rentalDays;
    protected Boolean isResolved;
    protected Double pricePerDay;
    protected LocalDate startDate;
    protected Integer numberOfGuests;

    public RentalFormObservable() {
        super();
    }

    public RentalFormObservable(Integer id,
                                Double amount,
                                Integer room_id,
                                Integer bill_id,
                                Integer guest_id,
                                Integer rental_days,
                                Boolean is_resolved,
                                Double price_per_day,
                                LocalDate start_date,
                                Integer number_of_guests,
                                LocalDateTime created_at,
                                LocalDateTime updated_at) {

        super(id, created_at, updated_at);

        this.amount = amount;
        this.roomId = room_id;
        this.billId = bill_id;
        this.guestId = guest_id;
        this.startDate = start_date;
        this.rentalDays = rental_days;
        this.isResolved = is_resolved;
        this.pricePerDay = price_per_day;
        this.numberOfGuests = number_of_guests;

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
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
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
            setStartDate(LocalDate.parse(startDateString));
            notifyPropertyChanged(BR.startDateString);
        } catch (NullPointerException | IllegalArgumentException e) {
            setStartDate(null);
        } catch (Exception e) {
            setStartDate(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        notifyPropertyChanged(BR.numberOfGuests);
    }

    @Bindable
    public String getNumberOfGuestsString() {
        try {
            return numberOfGuests.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setNumberOfGuestsString(String numberOfGuestsString) {
        try {
            setNumberOfGuests(Integer.valueOf(numberOfGuestsString));
        } catch (NullPointerException | NumberFormatException e) {
            setNumberOfGuests(null);
        } catch (Exception e) {
            setNumberOfGuests(null);
            e.printStackTrace();
        }
    }

    @NonNull
    public Boolean customizedEquals(@NonNull RentalFormObservable rentalFormObservable) throws IllegalAccessException {
        return ExtendedObservable.customizedEquals(this, rentalFormObservable);
    }

    @NonNull
    public RentalFormObservable customizedClone() {
        return ExtendedObservable.customizedClone(this);
    }

}
