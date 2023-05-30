package com.example.hotelmanagement.viewmodels;

import android.widget.Toast;

import com.example.hotelmanagement.ActivityMain;
import com.example.hotelmanagement.observables.GuestObservable;

public class GuestViewModel extends ExtendedViewModel<GuestObservable> {

    public GuestViewModel() {
        super();
    }
    public void checkObservable(GuestObservable guestObservable){
        if (guestObservable.getIdNumber() == null || guestObservable.getIdNumber().equals("")){
            Toast.makeText(ActivityMain.getInstance(), "ID number is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guestObservable.getName() == null || guestObservable.getName().equals("")){
            Toast.makeText(ActivityMain.getInstance(), "Name is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guestObservable.getPhoneNumber() == null || guestObservable.getPhoneNumber().equals("")){
            Toast.makeText(ActivityMain.getInstance(), "Phone number is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guestObservable.getAddress() == null || guestObservable.getAddress().equals("")){
            Toast.makeText(ActivityMain.getInstance(), "Address is missing", Toast.LENGTH_SHORT).show();
            return;
        }

    }
    @Override
    public void loadData() {
        // Call Hasura to query all the data

        dataLoaded = true;
    }

}
