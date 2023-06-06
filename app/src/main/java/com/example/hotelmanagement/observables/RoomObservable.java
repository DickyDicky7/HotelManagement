package com.example.hotelmanagement.observables;

import androidx.databinding.Bindable;

import com.example.hotelmanagement.BR;

import java.sql.Timestamp;

public class RoomObservable extends ExtendedObservable {

    protected String name;
    protected String note;
    protected String description;
    protected Boolean isOccupied;
    protected Integer roomKindId;

    public RoomObservable() {
        super();
    }

    public RoomObservable(Integer id,
                          String name,
                          String note,
                          Boolean isOccupied,
                          Integer roomKindId,
                          String description,
                          Timestamp createdAt,
                          Timestamp updatedAt) {

        super(id, createdAt, updatedAt);

        this.name = name;
        this.note = note;
        this.isOccupied = isOccupied;
        this.roomKindId = roomKindId;
        this.description = description;

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
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
        notifyPropertyChanged(BR.note);
    }

    @Bindable
    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean isOccupied) {
        this.isOccupied = isOccupied;
        notifyPropertyChanged(BR.isOccupied);
    }

    @Bindable
    public Integer getRoomKindId() {
        return roomKindId;
    }

    public void setRoomKindId(Integer roomKindId) {
        this.roomKindId = roomKindId;
        notifyPropertyChanged(BR.roomKindId);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

}
