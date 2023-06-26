package com.example.hotelmanagement.observables;

import androidx.annotation.NonNull;
import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.time.LocalDateTime;

public class RoomKindObservable extends ExtendedObservable {

    protected String name;
    protected Double area;
    protected Double price;
    protected String imageURL;
    protected Integer capacity;
    protected Integer numberOfBeds;
    protected Double surchargePercentage;

    public RoomKindObservable() {
        super();
    }

    public RoomKindObservable(Integer id,
                              String name,
                              Double area,
                              Double price,
                              String image_url,
                              Integer capacity,
                              Integer number_of_beds,
                              LocalDateTime created_at,
                              LocalDateTime updated_at,
                              Double surcharge_percentage) {

        super(id, created_at, updated_at);

        this.name = name;
        this.area = area;
        this.price = price;
        this.capacity = capacity;
        this.imageURL = image_url;
        this.numberOfBeds = number_of_beds;
        this.surchargePercentage = surcharge_percentage;

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
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    public String getPriceString() {
        try {
            return price.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setPriceString(String priceString) {
        try {
            setPrice(Double.valueOf(priceString));
        } catch (NullPointerException | NumberFormatException e) {
            setPrice(null);
        } catch (Exception e) {
            setPrice(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        notifyPropertyChanged(BR.capacity);
    }

    @Bindable
    public String getCapacityString() {
        try {
            return capacity.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setCapacityString(String capacityString) {
        try {
            setCapacity(Integer.valueOf(capacityString));
        } catch (NullPointerException | NumberFormatException e) {
            setCapacity(null);
        } catch (Exception e) {
            setCapacity(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public Integer getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(Integer numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
        notifyPropertyChanged(BR.numberOfBeds);
    }

    @Bindable
    public String getNumberOfBedsString() {
        try {
            return numberOfBeds.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setNumberOfBedsString(String numberOfBedsString) {
        try {
            setNumberOfBeds(Integer.valueOf(numberOfBedsString));
        } catch (NullPointerException | NumberFormatException e) {
            setNumberOfBeds(null);
        } catch (Exception e) {
            setNumberOfBeds(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
        notifyPropertyChanged(BR.area);
    }

    @Bindable
    public String getAreaString() {
        try {
            return area.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setAreaString(String areaString) {
        try {
            setArea(Double.valueOf(areaString));
        } catch (NullPointerException | NumberFormatException e) {
            setArea(null);
        } catch (Exception e) {
            setArea(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public Double getSurchargePercentage() {
        return surchargePercentage;
    }

    public void setSurchargePercentage(Double surchargePercentage) {
        this.surchargePercentage = surchargePercentage;
        notifyPropertyChanged(BR.surchargePercentage);
    }

    @Bindable
    public String getSurchargePercentageString() {
        try {
            return surchargePercentage.toString();
        } catch (NullPointerException e) {
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setSurchargePercentageString(String surchargePercentageString) {
        try {
            setSurchargePercentage(Double.valueOf(surchargePercentageString));
        } catch (NullPointerException | NumberFormatException e) {
            setSurchargePercentage(null);
        } catch (Exception e) {
            setSurchargePercentage(null);
            e.printStackTrace();
        }
    }

    @Bindable
    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
        notifyPropertyChanged(BR.imageURL);
    }

    @NonNull
    public Boolean customizedEquals(@NonNull RoomKindObservable roomKindObservable) throws IllegalAccessException {
        return ExtendedObservable.customizedEquals(this, roomKindObservable);
    }

    @NonNull
    public RoomKindObservable customizedClone() {
        return ExtendedObservable.customizedClone(this);
    }

}
