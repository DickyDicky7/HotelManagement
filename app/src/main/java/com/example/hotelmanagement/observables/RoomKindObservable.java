package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class RoomKindObservable extends ExtendedObservable {

    protected String name;
    protected BigDecimal price;
    protected Integer capacity;
    protected Double surchargePercentage;
    protected Integer numOfBed;
    protected Double area;
    public RoomKindObservable(){
        super();
    }
    public RoomKindObservable(Integer id,
                              String name,
                              BigDecimal price,
                              Integer capacity,
                              Integer numOfBed,
                              Double area,
                              Timestamp createdAt,
                              Timestamp updatedAt,
                              Double surchargePercentage) {

        super(id, createdAt, updatedAt);

        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.numOfBed=numOfBed;
        this.area=area;
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
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        notifyPropertyChanged(BR.priceString);
    }
    @Bindable
    public String getPriceString(){
        return price.toString();
    }
    public void setPriceString(String priceString){
        this.setPrice(new BigDecimal(priceString));
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
    public String getCapacityString(){
        return capacity.toString();
    }
    public void setCapacityString(String capacityString){
        this.setCapacity(Integer.valueOf(capacityString));
    }
    //number of bed
    @Bindable
    public Integer getNumOfBed(){
        return this.numOfBed;
    }
    public void setNumOfBed(Integer numOfBed){
        this.numOfBed=numOfBed;
        notifyPropertyChanged(BR.numOfBedString);
    }
    @Bindable
    public String getNumOfBedString(){
        return this.numOfBed.toString();
    }
    public void setNumOfBedString(String numOfBedString){
        this.setNumOfBed(Integer.valueOf(numOfBedString));
    }
    //area
    @Bindable
    public Double getArea(){
        return this.area;
    }
    public void setArea(Double area){
        this.area=area;
        notifyPropertyChanged(BR.areaString);
    }
    @Bindable
    public String getAreaString(){
        return this.area.toString();
    }
    public void setAreaString(String areaString){
        setArea(Double.valueOf(areaString));
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
    public String getSurchargePercentageString(){
        return this.surchargePercentage.toString();
    }
    public void setSurchargePercentageString(String surchargePercentageString){
        this.setSurchargePercentage(Double.valueOf(surchargePercentageString));
    }
}
