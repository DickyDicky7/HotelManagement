package com.example.hotelmanagement.viewmodels;

import com.example.hotelmanagement.observables.GuestKindObservable;

import java.util.List;
import java.util.Random;

public class GuestKindViewModel extends ExtendedViewModel<GuestKindObservable> {

    public GuestKindViewModel() {
        super();
    }

    @Override
    public void loadData() {
        // Call Hasura to query all the data
        List<GuestKindObservable> guestKindObservables = modelState.getValue();
        Random random = new Random();
        int temp = random.nextInt(100);
        for (int i = 1; i <= temp; i++) {
            guestKindObservables.add(new GuestKindObservable());
        }
        dataLoaded = true;
    }

}
