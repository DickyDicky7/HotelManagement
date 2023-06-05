package com.example.hotelmanagement.viewmodels;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Guest_AllQuery;
import com.example.hasura.Guest_InsertMutation;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.ActivityMain;
import com.example.hotelmanagement.observables.GuestObservable;

import java.sql.Timestamp;
import java.util.List;

public class GuestViewModel extends ExtendedViewModel<GuestObservable> {

    public GuestViewModel() {
        super();
    }

    public void checkObservable(GuestObservable guestObservable) {
        if (guestObservable.getIdNumber() == null || guestObservable.getIdNumber().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "ID number is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guestObservable.getName() == null || guestObservable.getName().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Name is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guestObservable.getPhoneNumber() == null || guestObservable.getPhoneNumber().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Phone number is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guestObservable.getAddress() == null || guestObservable.getAddress().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Address is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (guestObservable.getGuestKindId() == null || guestObservable.getGuestKindId().toString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Kind of guest is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        insert(guestObservable);
    }

    public void insert(GuestObservable guestObservable) {
        Guest_InsertMutation guestInsertMutation = Guest_InsertMutation
                .builder()
                .name(guestObservable.getName())
                .idNumber(guestObservable.getIdNumber())
                .address(guestObservable.getAddress())
                .guestKindId(guestObservable.getGuestKindId())
                .phoneNumber(guestObservable.getPhoneNumber())
                .build();
        Hasura.apolloClient.mutate(guestInsertMutation)
                .enqueue(new ApolloCall.Callback<Guest_InsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Guest_InsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<GuestObservable> temp = modelState.getValue();
                            temp.add(guestObservable);
                            modelState.postValue(temp);
                        }
                        if (response.getErrors() != null) {
                            System.out.println(response.getErrors());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void loadData() {
        // Call Hasura to query all the data
        Hasura.apolloClient.query(new Guest_AllQuery())
                .enqueue(new ApolloCall.Callback<Guest_AllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Guest_AllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<GuestObservable> guestObservables = modelState.getValue();
                            response.getData().GUEST().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                GuestObservable temp = new GuestObservable(
                                        item.id(),
                                        item.name(),
                                        item.address(),
                                        item.id_number(),
                                        item.guestkind_id(),
                                        item.phone_number(),
                                        createdAt,
                                        updatedAt
                                );
                            });
                            modelState.postValue(guestObservables);
                        }
                        if (response.getErrors() != null) {
                            System.out.println(response.getErrors());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
        dataLoaded = true;
    }

}
