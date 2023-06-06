package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.RoomKindAllQuery;
import com.example.hasura.RoomKindInsertMutation;
import com.example.hotelmanagement.observables.RoomKindObservable;

import java.sql.Timestamp;
import java.util.List;

public class RoomKindViewModel extends ExtendedViewModel<RoomKindObservable> {

    public RoomKindViewModel() {
        super();
    }

    private void insert(RoomKindObservable observable) {
        RoomKindInsertMutation roomKindInsertMutation = RoomKindInsertMutation
                .builder()
                .name(observable.getName())
                .price(observable.getPrice())
                .capacity(observable.getCapacity())
                .numOfBed(observable.getNumOfBed())
                .area(observable.getArea())
                .surchargePercentage(observable.getSurchargePercentage())
                .build();
        Hasura.apolloClient.mutate(roomKindInsertMutation)
                .enqueue(new ApolloCall.Callback<RoomKindInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomKindInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<RoomKindObservable> temp = modelState.getValue();
                            temp.add(observable);
                            modelState.postValue(temp);
                            System.out.println(response.getData().insert_ROOMKIND());

                            if (onSuccessCallback != null)
                                onSuccessCallback.accept(null);

                        }
                        if (response.getErrors() != null) {
                            System.out.println(response.getErrors());

                            if (onFailureCallback != null)
                                onFailureCallback.accept(null);

                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();

                        if (onFailureCallback != null)
                            onFailureCallback.accept(null);

                    }
                });
    }

    @Override
    public void loadData() {
        // Call Hasura to query all the data
        Hasura.apolloClient.query(new RoomKindAllQuery())
                .enqueue(new ApolloCall.Callback<RoomKindAllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomKindAllQuery.Data> response) {
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
                                roomKindObservableList.add(temp);
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
    }

}
