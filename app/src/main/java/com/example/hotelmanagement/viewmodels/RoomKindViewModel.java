package com.example.hotelmanagement.viewmodels;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.RoomKind_AllQuery;
import com.example.hasura.RoomKind_InsertMutation;
import com.example.hotelmanagement.ActivityMain;
import com.example.hotelmanagement.observables.RoomKindObservable;

import java.sql.Timestamp;
import java.util.List;

public class RoomKindViewModel extends ExtendedViewModel<RoomKindObservable> {

    public RoomKindViewModel() {
        super();
    }

    public void checkObservable(RoomKindObservable observable) {
        if (observable.getName().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Name is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (observable.getPriceString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Price is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (observable.getCapacityString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Capacity is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (observable.getNumOfBedString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Number of bed is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (observable.getAreaString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Area is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (observable.getSurchargePercentageString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Surcharge percentage is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        insert(observable);
    }

    private void insert(RoomKindObservable observable) {
        RoomKind_InsertMutation roomKindInsertMutation = RoomKind_InsertMutation
                .builder()
                .name(observable.getName())
                .price(observable.getPrice())
                .capacity(observable.getCapacity())
                .numOfBed(observable.getNumOfBed())
                .area(observable.getArea())
                .surchargePercentage(observable.getSurchargePercentage())
                .build();
        Hasura.apolloClient.mutate(roomKindInsertMutation)
                .enqueue(new ApolloCall.Callback<RoomKind_InsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomKind_InsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<RoomKindObservable> temp = modelState.getValue();
                            temp.add(observable);
                            modelState.postValue(temp);
                            System.out.println(response.getData().insert_ROOMKIND());
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
        Hasura.apolloClient.query(new RoomKind_AllQuery())
                .enqueue(new ApolloCall.Callback<RoomKind_AllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomKind_AllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<RoomKindObservable> roomKindObservableList = modelState.getValue();
                            response.getData().ROOMKIND().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                RoomKindObservable temp = new RoomKindObservable(
                                        item.id(),
                                        item.name(),
                                        item.area(),
                                        item.price(),
                                        item.capacity(),
                                        item.number_of_beds(),
                                        createdAt,
                                        updatedAt,
                                        item.surcharge_percentage()
                                );
                            });
                            modelState.postValue(roomKindObservableList);
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
