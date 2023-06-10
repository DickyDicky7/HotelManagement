package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.RoomAllQuery;
import com.example.hasura.RoomInsertMutation;
import com.example.hotelmanagement.observables.RoomObservable;

import java.time.LocalDateTime;
import java.util.List;

public class RoomViewModel extends ExtendedViewModel<RoomObservable> {

    public RoomViewModel() {
        super();
    }

    public void insert(RoomObservable roomObservable) {
        RoomInsertMutation roomInsertMutation = RoomInsertMutation
                .builder()
                .name(roomObservable.getName())
                .note(roomObservable.getNote())
                .roomKindId(roomObservable.getRoomKindId())
                .isOccupied(roomObservable.getIsOccupied())
                .description(roomObservable.getDescription())
                .build();
        Hasura.apolloClient.mutate(roomInsertMutation)
                .enqueue(new ApolloCall.Callback<RoomInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<RoomObservable> roomObservables = modelState.getValue();
                            if (roomObservables != null) {
                                roomObservables.add(roomObservable);
                                modelState.postValue(roomObservables);
                            }
                            if (onSuccessCallback != null) {
                                onSuccessCallback.accept(null);
                                onSuccessCallback = null;
                            }
                            System.out.println(response.getData().insert_ROOM());
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
        Hasura.apolloClient.query(new RoomAllQuery())
                .enqueue(new ApolloCall.Callback<RoomAllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomAllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<RoomObservable> roomObservables = modelState.getValue();
                            response.getData().ROOM().forEach(item -> {
                                LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                                LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                                RoomObservable roomObservable = new RoomObservable(
                                        item.id(),
                                        item.name(),
                                        item.note(),
                                        item.description(),
                                        item.is_occupied(),
                                        item.roomkind_id(),
                                        item_created_at,
                                        item_updated_at
                                );
                                if (roomObservables != null) {
                                    roomObservables.add(roomObservable);
                                }
                            });
                            if (roomObservables != null) {
                                modelState.postValue(roomObservables);
                            }
                        }
                        if (response.getErrors() != null) {
                            System.out.println((response.getErrors()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

}
