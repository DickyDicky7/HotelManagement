package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.RoomInsertMutation;
import com.example.hasura.RoomSubscription;
import com.example.hasura.RoomUpdateByIdMutation;
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
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            RoomInsertMutation.Insert_ROOM insert_room = response.getData().insert_ROOM();
                            if (insert_room != null) {
                                Log.d("RoomViewModel Insert Response Debug", insert_room.toString());
                            }
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

    public void update(RoomObservable roomObservable) {
        RoomUpdateByIdMutation roomUpdateByIdMutation = RoomUpdateByIdMutation
                .builder()
                .id(roomObservable.getId())
                .name(roomObservable.getName())
                .note(roomObservable.getNote())
                .isOccupied(roomObservable.getIsOccupied())
                .roomkind_id(roomObservable.getRoomKindId())
                .description(roomObservable.getDescription())
                .build();
        Hasura.apolloClient.mutate(roomUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<RoomUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            RoomUpdateByIdMutation.Update_ROOM update_room = response.getData().update_ROOM();
                            if (update_room != null) {
                                Log.d("RoomViewModel Update Response Debug", update_room.toString());
                            }
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

    @NonNull
    public String getRoomName(@Nullable Integer id) {
        RoomObservable roomObservable = getObservable(id);
        if (roomObservable != null && roomObservable.getName() != null) {
            return roomObservable.getName();
        }
        return "";
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
                        notifySubscriptionObservers();
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
