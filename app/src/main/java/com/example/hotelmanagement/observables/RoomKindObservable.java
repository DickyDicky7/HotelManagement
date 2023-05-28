package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class RoomKindObservable extends ExtendedObservable {

    protected String name;
    protected Double area;
    protected Double price;
    protected Integer capacity;
    protected Integer numOfBed;
    protected Double surchargePercentage;

    public RoomKindObservable() {
        super();
    }

    public RoomKindObservable(Integer id,
                              String name,
                              Double area,
                              Double price,
                              Integer capacity,
                              Integer numOfBed,
                              Timestamp createdAt,
                              Timestamp updatedAt,
                              Double surchargePercentage) {

        super(id, createdAt, updatedAt);

        this.name = name;
        this.area = area;
        this.price = price;
        this.capacity = capacity;
        this.numOfBed = numOfBed;
        this.surchargePercentage = surchargePercentage;
    }

    //name
    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    //price
    @Bindable
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
        notifyPropertyChanged(BR.priceString);
    }

    @Bindable
    public String getPriceString() {
        try {
            return price.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setPriceString(String priceString) {
        try {
            setPrice(Double.valueOf(priceString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //capacity
    @Bindable
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
        notifyPropertyChanged(BR.capacityString);
    }

    @Bindable
    public String getCapacityString() {
        try {
            return capacity.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setCapacityString(String capacityString) {
        try {
            setCapacity(Integer.valueOf(capacityString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //number of bed
    @Bindable
    public Integer getNumOfBed() {
        return this.numOfBed;
    }

    public void setNumOfBed(Integer numOfBed) {
        this.numOfBed = numOfBed;
        notifyPropertyChanged(BR.numOfBedString);
    }

    @Bindable
    public String getNumOfBedString() {
        try {
            return numOfBed.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setNumOfBedString(String numOfBedString) {
        try {
            setNumOfBed(Integer.valueOf(numOfBedString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //area
    @Bindable
    public Double getArea() {
        return this.area;
    }

    public void setArea(Double area) {
        this.area = area;
        notifyPropertyChanged(BR.areaString);
    }

    @Bindable
    public String getAreaString() {
        try {
            return area.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setAreaString(String areaString) {
        try {
            setArea(Double.valueOf(areaString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bindable
    public Double getSurchargePercentage() {
        return surchargePercentage;
    }

    public void setSurchargePercentage(Double surchargePercentage) {
        this.surchargePercentage = surchargePercentage;
        notifyPropertyChanged(BR.surchargePercentageString);
    }

    @Bindable
    public String getSurchargePercentageString() {
        try {
            return surchargePercentage.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void setSurchargePercentageString(String surchargePercentageString) {
        try {
            setSurchargePercentage(Double.valueOf(surchargePercentageString));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
