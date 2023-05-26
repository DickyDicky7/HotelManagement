package com.example.hotelmanagement.viewmodels;

import com.example.hotelmanagement.observables.RentalFormObservable;

public class RentalFormViewModel extends ExtendedViewModel<RentalFormObservable> {

    public RentalFormViewModel() {
        super();
    }

    @Override
    public void loadData() {
        // Call Hasura to query all the data
        dataLoaded = true;
    }

}
