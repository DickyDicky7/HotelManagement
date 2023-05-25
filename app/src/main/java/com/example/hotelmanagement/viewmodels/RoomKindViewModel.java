package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.RoomKind_InsertMutation;
import com.example.hotelmanagement.observables.RoomKindObservable;

import java.util.List;

public class RoomKindViewModel extends ExtendedViewModel<RoomKindObservable> {
    //List<BillObservable> billObservables = modelState.getValue();
    //billObservables.add(/* bill observable ỡ dây */);
    // Nếu ko dùng thread
    //modelState.setValue(billObservables);
    // Nếu dùng thread
    //modelState.postValue(billObservables);
    public RoomKindViewModel() {
        super();
    }

    public void checkObservable(RoomKindObservable observable) {
        if (observable.getName().equals("")) {
            observable.setName(null);
        }
        if (observable.getPriceString().equals("")) {
            observable.setPrice(null);
        }
        if (observable.getCapacityString().equals("")) {
            observable.setCapacity(null);
        }
        if (observable.getNumOfBedString().equals("")) {
            observable.setNumOfBed(null);
        }
        if (observable.getAreaString().equals("")) {
            observable.setArea(null);
        }
        if (observable.getSurchargePercentageString().equals("")) {
            observable.setSurchargePercentage(null);
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
                        List<RoomKindObservable> temp = modelState.getValue();
                        temp.add(observable);
                        modelState.postValue(temp);
                        System.out.println(response.getData().insert_ROOMKIND());
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        System.out.println(e);
                    }
                });
    }
}
