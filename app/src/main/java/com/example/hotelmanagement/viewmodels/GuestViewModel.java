package com.example.hotelmanagement.viewmodels;

import com.example.hotelmanagement.observables.GuestObservable;

public class GuestViewModel extends ExtendedViewModel<GuestObservable> {

    public GuestViewModel() {
        super();
    }

    @Override
    public void loadData() {
        // Call Hasura to query all the data

        dataLoaded = true;
    }

}
