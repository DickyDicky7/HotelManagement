package com.example.hotelmanagement.viewmodels;

import com.example.hotelmanagement.observables.RoomObservable;

public class RoomViewModel extends ExtendedViewModel<RoomObservable> {

    public RoomViewModel() {
        super();
    }

    @Override
    public void loadData() {
        // Call Hasura to query all the data
        dataLoaded = true;
    }

}
