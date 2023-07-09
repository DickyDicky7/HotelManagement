package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.RoomDeleteByIdMutation;
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

    @Override
    public void insert(@NonNull RoomObservable roomObservable) {
        RoomInsertMutation roomInsertMutation = RoomInsertMutation
                .builder()
                .name(roomObservable.getName())
                .note(roomObservable.getNote())
                .roomKindId(roomObservable.getRoomKindId())
                .isOccupied(roomObservable.getIsOccupied())
                .description(roomObservable.getDescription())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(roomInsertMutation)
                .enqueue(new ApolloCall.Callback<RoomInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            RoomInsertMutation.Insert_ROOM_one insert_room_one = response.getData().insert_ROOM_one();
                            if (insert_room_one != null) {
                                Log.d("RoomViewModel Insert Response Debug", insert_room_one.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("RoomViewModel Insert Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        onFailureHandler();
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void update(@NonNull RoomObservable usedRoomObservable, @NonNull RoomObservable copyRoomObservable) {
        RoomUpdateByIdMutation roomUpdateByIdMutation = RoomUpdateByIdMutation
                .builder()
                .id(usedRoomObservable.getId())
                .name(usedRoomObservable.getName())
                .note(usedRoomObservable.getNote())
                .roomKindId(usedRoomObservable.getRoomKindId())
                .isOccupied(usedRoomObservable.getIsOccupied())
                .description(usedRoomObservable.getDescription())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(roomUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<RoomUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            RoomUpdateByIdMutation.Update_ROOM_by_pk update_room_by_pk = response.getData().update_ROOM_by_pk();
                            if (update_room_by_pk != null) {
                                Log.d("RoomViewModel Update Response Debug", update_room_by_pk.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("RoomViewModel Update Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        onFailureHandler();
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void delete(@NonNull RoomObservable roomObservable) {
        RoomDeleteByIdMutation roomDeleteByIdMutation = RoomDeleteByIdMutation
                .builder()
                .id(roomObservable.getId())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(roomDeleteByIdMutation)
                .enqueue(new ApolloCall.Callback<RoomDeleteByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomDeleteByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            RoomDeleteByIdMutation.Delete_ROOM_by_pk delete_room_by_pk = response.getData().delete_ROOM_by_pk();
                            if (delete_room_by_pk != null) {
                                Log.d("RoomViewModel Delete Response Debug", delete_room_by_pk.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("RoomViewModel Delete Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        onFailureHandler();
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
        Hasura.requireInstance().requireApolloClient().subscribe(new RoomSubscription()).execute(new ApolloSubscriptionCall.Callback<RoomSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<RoomSubscription.Data> response) {
                if (response.getData() != null) {
                    List<RoomObservable> roomObservables = modelState.getValue();
                    if (roomObservables != null) {
                        roomObservables.clear();
                    }
                    response.getData().ROOM().forEach(item -> {
                        RoomObservable roomObservable = new RoomObservable(
                                item.id(),
                                item.name(),
                                item.note(),
                                item.description(),
                                item.is_occupied(),
                                item.room_kind_id(),
                                LocalDateTime.parse(item.created_at().toString()),
                                LocalDateTime.parse(item.updated_at().toString())
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
