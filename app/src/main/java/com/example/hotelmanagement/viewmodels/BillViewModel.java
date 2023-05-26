package com.example.hotelmanagement.viewmodels;

import com.example.hotelmanagement.observables.BillObservable;

public class BillViewModel extends ExtendedViewModel<BillObservable> {

    public BillViewModel() {
        super();
    }

    @Override
    public void loadData() {
        // Call Hasura to query all the data
        dataLoaded = true;
    }

}
