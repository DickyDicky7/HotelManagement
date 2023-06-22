package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.RoomInsertMutation;
import com.example.hasura.RoomSubscription;
import com.example.hasura.RoomByIdQuery;
import com.example.hasura.RoomUpdateByIdMutation;
import com.example.hasura.RoomkindUpdateByIdMutation;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;

import java.sql.Timestamp;
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
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            Log.d("RoomViewModel Insert Response Debug", response.getData().insert_ROOM().toString());
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("RoomViewModel Insert Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        if (onFailureCallback != null) {
                            onFailureCallback.run();
                            onFailureCallback = null;
                        }
                        e.printStackTrace();
                    }
                });
    }
    public void update(RoomObservable roomObservable,int id){
        RoomUpdateByIdMutation room_update_by_id_mutation = RoomUpdateByIdMutation
                .builder()
                .id(id)
                .name(roomObservable.getName())
                .note(roomObservable.getNote())
                .description(roomObservable.getDescription())
                .roomkind_id(roomObservable.getRoomKindId())
                .isOccupied(roomObservable.getIsOccupied())
                .build();
        Hasura.apolloClient.mutate(room_update_by_id_mutation)
                .enqueue(new ApolloCall.Callback<RoomUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            List<RoomObservable> temp = modelState.getValue();

                            for (int j =0; j< temp.size(); j++) {
                                if (id == temp.get(j).getId()) temp.set(j, roomObservable);
                            }
                            modelState.postValue(temp);
                            Log.d("RoomViewModel Update Response Debug", response.getData().update_ROOM().toString());
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("RoomViewModel update Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        if (onFailureCallback != null) {
                            onFailureCallback.run();
                            onFailureCallback = null;
                        }
                        e.printStackTrace();
                    }
                });
    }

    public void filldata(int i, RoomObservable roomObservable){

        Hasura.apolloClient.query(new RoomByIdQuery(new Input<Integer>(i, true)))
                .enqueue(new ApolloCall.Callback<RoomByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            response.getData().ROOM().forEach(item -> {
                                LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                                LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                                roomObservable.setId(item.id());
                                roomObservable.setName(item.name());
                                roomObservable.setNote(item.note());
                                roomObservable.setDescription(item.description());
                                roomObservable.setIsOccupied(item.is_occupied());
                                roomObservable.setRoomKindId(item.roomkind_id());
                                roomObservable.setCreatedAt(item_created_at);
                                roomObservable.setUpdatedAt(item_updated_at);

                            });
                            ;

                        }
                        if (response.getErrors() != null) {

                            System.out.println(response.getErrors());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {

                    }
                });

    }
    public RoomObservable roomname(int id){
        RoomObservable roomObservable = new RoomObservable();
        List<RoomObservable> temp = modelState.getValue();

        for (int j =0; j< temp.size(); j++) {
            if (id == temp.get(j).getId()) {
                roomObservable = temp.get(j);
                break;
            }
        }
        return roomObservable;
    }
    @Override
    public void startSubscription() {
        Hasura.apolloClient.subscribe(new RoomSubscription()).execute(new ApolloSubscriptionCall.Callback<RoomSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<RoomSubscription.Data> response) {
                if (response.getData() != null) {
                    List<RoomObservable> roomObservables = modelState.getValue();
                    if (roomObservables != null) {
                        roomObservables.clear();
                    }
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
                    response.getErrors().forEach(error -> Log.e("RoomViewModel Subscription Error", error.toString()));
                }
            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                e.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Log.i("RoomViewModel Subscription Info", "Subscription Completed");
            }

            @Override
            public void onTerminated() {
                Log.i("RoomViewModel Subscription Info", "Subscription Terminated");
            }

            @Override
            public void onConnected() {
                Log.i("RoomViewModel Subscription Info", "Subscription Connected");
            }
        });
    }

}
