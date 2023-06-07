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

    public void insert(RoomKindObservable roomKindObservable) {
        RoomKindInsertMutation roomKindInsertMutation = RoomKindInsertMutation
                .builder()
                .name(roomKindObservable.getName())
                .area(roomKindObservable.getArea())
                .price(roomKindObservable.getPrice())
                .capacity(roomKindObservable.getCapacity())
                .numOfBed(roomKindObservable.getNumOfBed())
                .surchargePercentage(roomKindObservable.getSurchargePercentage())
                .build();
        Hasura.apolloClient.mutate(roomKindInsertMutation)
                .enqueue(new ApolloCall.Callback<RoomKindInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomKindInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<RoomKindObservable> roomKindObservables = modelState.getValue();
                            if (roomKindObservables != null) {
                                roomKindObservables.add(roomKindObservable);
                                modelState.postValue(roomKindObservables);
                            }
                            if (onSuccessCallback != null) {
                                onSuccessCallback.accept(null);
                                onSuccessCallback = null;
                            }
                            System.out.println(response.getData().insert_ROOMKIND());
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.accept(null);
                                onFailureCallback = null;
                            }
                            System.out.println(response.getErrors());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        if (onFailureCallback != null) {
                            onFailureCallback.accept(null);
                            onFailureCallback = null;
                        }
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void loadData() {
        Hasura.apolloClient.query(new RoomKindAllQuery())
                .enqueue(new ApolloCall.Callback<RoomKindAllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomKindAllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<RoomKindObservable> roomKindObservables = modelState.getValue();
                            response.getData().ROOMKIND().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                RoomKindObservable roomKindObservable = new RoomKindObservable(
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
                                if (roomKindObservables != null) {
                                    roomKindObservables.add(roomKindObservable);
                                }
                            });
                            if (roomKindObservables != null) {
                                modelState.postValue(roomKindObservables);
                            }
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
