package com.example.hotelmanagement.viewmodels;

import com.example.hotelmanagement.observables.RoomKindObservable;

public class RoomKindViewModel extends ExtendedViewModel<RoomKindObservable> {

    public RoomKindViewModel() {
        super();
    }
    public void checkObservable(RoomKindObservable observable){
        if (observable.getName().equals("")){
            observable.setName(null);
        }
        if (observable.getPrice().toString().equals("")){
            observable.setPrice(null);
        }
        if (observable.getCapacity().toString().equals("")){
            observable.setCapacity(null);
        }
        if (observable.getNumOfBed().toString().equals("")){
            observable.setNumOfBed(null);
        }
        if (observable.getArea().toString().equals("")){
            observable.setArea(null);
        }
        if (observable.getSurchargePercentage().toString().equals("")){
            observable.setSurchargePercentage(null);
        }

    }

}
