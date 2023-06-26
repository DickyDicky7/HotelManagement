package com.example.hotelmanagement.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hasura.Hasura;
import com.example.hasura.RoomKindInsertMutation;
import com.example.hasura.RoomKindSubscription;
import com.example.hasura.RoomKindUpdateByIdMutation;
import com.example.hotelmanagement.observables.RoomKindObservable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class RoomKindViewModel extends ExtendedViewModel<RoomKindObservable> {

    public RoomKindViewModel() {
        super();
    }

    public void insert(RoomKindObservable roomKindObservable) {
        if (MediaManager.get() == null) {
            return;
        }
        MediaManager.get().upload(Uri.parse(roomKindObservable.getImageURL())).option("folder", "ParadisePalms_HMS").callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.i("RoomKindViewModel Upload Image Info", "Start");
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.i("RoomKindViewModel Upload Image Info", "Progress");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                roomKindObservable.setImageURL(resultData.getOrDefault("secure_url", "").toString());
                RoomKindInsertMutation roomKindInsertMutation = RoomKindInsertMutation
                        .builder()
                        .name(roomKindObservable.getName())
                        .area(roomKindObservable.getArea())
                        .price(roomKindObservable.getPrice())
                        .imageUrl(roomKindObservable.getImageURL())
                        .capacity(roomKindObservable.getCapacity())
                        .numberOfBeds(roomKindObservable.getNumberOfBeds())
                        .surchargePercentage(roomKindObservable.getSurchargePercentage())
                        .build();
                Hasura.apolloClient.mutate(roomKindInsertMutation)
                        .enqueue(new ApolloCall.Callback<RoomKindInsertMutation.Data>() {
                            @Override
                            public void onResponse(@NonNull Response<RoomKindInsertMutation.Data> response) {
                                if (response.getData() != null) {
                                    if (onSuccessCallback != null) {
                                        onSuccessCallback.run();
                                        onSuccessCallback = null;
                                    }
                                    RoomKindInsertMutation.Insert_ROOMKIND insert_roomkind = response.getData().insert_ROOMKIND();
                                    if (insert_roomkind != null) {
                                        Log.d("RoomKindViewModel Insert Response Debug", insert_roomkind.toString());
                                    }
                                }
                                if (response.getErrors() != null) {
                                    if (onFailureCallback != null) {
                                        onFailureCallback.run();
                                        onFailureCallback = null;
                                    }
                                    response.getErrors().forEach(error -> Log.e("RoomKindViewModel Insert Error", error.toString()));
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

            @Override
            public void onError(String requestId, ErrorInfo error) {
                Log.e("RoomKindViewModel Upload Image Error", error.toString());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.e("RoomKindViewModel Upload Image Error", error.toString());
            }
        }).dispatch();
    }

    public void update(RoomKindObservable usedRoomKindObservable, RoomKindObservable copyRoomKindObservable) {
        if (usedRoomKindObservable.getImageURL().equals(copyRoomKindObservable.getImageURL())) {
            RoomKindUpdateByIdMutation roomkindUpdateByIdMutation = RoomKindUpdateByIdMutation
                    .builder()
                    .id(usedRoomKindObservable.getId())
                    .name(usedRoomKindObservable.getName())
                    .area(usedRoomKindObservable.getArea())
                    .price(usedRoomKindObservable.getPrice())
                    .imageUrl(usedRoomKindObservable.getImageURL())
                    .capacity(usedRoomKindObservable.getCapacity())
                    .numberOfBeds(usedRoomKindObservable.getNumberOfBeds())
                    .surchargePercentage(usedRoomKindObservable.getSurchargePercentage())
                    .build();
            Hasura.apolloClient.mutate(roomkindUpdateByIdMutation)
                    .enqueue(new ApolloCall.Callback<RoomKindUpdateByIdMutation.Data>() {
                        @Override
                        public void onResponse(@NonNull Response<RoomKindUpdateByIdMutation.Data> response) {
                            if (response.getData() != null) {
                                if (onSuccessCallback != null) {
                                    onSuccessCallback.run();
                                    onSuccessCallback = null;
                                }
                                RoomKindUpdateByIdMutation.Update_ROOMKIND update_roomkind = response.getData().update_ROOMKIND();
                                if (update_roomkind != null) {
                                    Log.d("RoomKindViewModel Update Response Debug", update_roomkind.toString());
                                }
                            }
                            if (response.getErrors() != null) {
                                if (onFailureCallback != null) {
                                    onFailureCallback.run();
                                    onFailureCallback = null;
                                }
                                response.getErrors().forEach(error -> Log.e("RoomKindViewModel Update Error", error.toString()));
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
        } else {
            if (MediaManager.get() == null) {
                return;
            }
            MediaManager.get().upload(Uri.parse(usedRoomKindObservable.getImageURL())).option("folder", "ParadisePalms_HMS").callback(new UploadCallback() {
                @Override
                public void onStart(String requestId) {
                    Log.i("RoomKindViewModel Upload Image Info", "Start");
                }

                @Override
                public void onProgress(String requestId, long bytes, long totalBytes) {
                    Log.i("RoomKindViewModel Upload Image Info", "Progress");
                }

                @Override
                public void onSuccess(String requestId, Map resultData) {
                    usedRoomKindObservable.setImageURL(resultData.getOrDefault("secure_url", "").toString());
                    RoomKindUpdateByIdMutation roomkindUpdateByIdMutation = RoomKindUpdateByIdMutation
                            .builder()
                            .id(usedRoomKindObservable.getId())
                            .name(usedRoomKindObservable.getName())
                            .area(usedRoomKindObservable.getArea())
                            .price(usedRoomKindObservable.getPrice())
                            .imageUrl(usedRoomKindObservable.getImageURL())
                            .capacity(usedRoomKindObservable.getCapacity())
                            .numberOfBeds(usedRoomKindObservable.getNumberOfBeds())
                            .surchargePercentage(usedRoomKindObservable.getSurchargePercentage())
                            .build();
                    Hasura.apolloClient.mutate(roomkindUpdateByIdMutation)
                            .enqueue(new ApolloCall.Callback<RoomKindUpdateByIdMutation.Data>() {
                                @Override
                                public void onResponse(@NonNull Response<RoomKindUpdateByIdMutation.Data> response) {
                                    if (response.getData() != null) {
                                        if (onSuccessCallback != null) {
                                            onSuccessCallback.run();
                                            onSuccessCallback = null;
                                        }
                                        RoomKindUpdateByIdMutation.Update_ROOMKIND update_roomkind = response.getData().update_ROOMKIND();
                                        if (update_roomkind != null) {
                                            Log.d("RoomKindViewModel Update Response Debug", update_roomkind.toString());
                                        }
                                    }
                                    if (response.getErrors() != null) {
                                        if (onFailureCallback != null) {
                                            onFailureCallback.run();
                                            onFailureCallback = null;
                                        }
                                        response.getErrors().forEach(error -> Log.e("RoomKindViewModel Update Error", error.toString()));
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

                @Override
                public void onError(String requestId, ErrorInfo error) {
                    Log.e("RoomKindViewModel Upload Image Error", error.toString());
                }

                @Override
                public void onReschedule(String requestId, ErrorInfo error) {
                    Log.e("RoomKindViewModel Upload Image Error", error.toString());
                }
            }).dispatch();
        }
    }

    @NonNull
    public String getRoomKindName(@Nullable Integer id) {
        RoomKindObservable roomKindObservable = getObservable(id);
        if (roomKindObservable != null && roomKindObservable.getName() != null) {
            return roomKindObservable.getName();
        }
        return "";
    }

    @Override
    public void startSubscription() {
        Hasura.apolloClient.subscribe(new RoomKindSubscription()).execute(new ApolloSubscriptionCall.Callback<RoomKindSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<RoomKindSubscription.Data> response) {
                if (response.getData() != null) {
                    List<RoomKindObservable> roomKindObservables = modelState.getValue();
                    if (roomKindObservables != null) {
                        roomKindObservables.clear();
                    }
                    response.getData().ROOMKIND().forEach(item -> {
                        LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                        LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                        RoomKindObservable roomKindObservable = new RoomKindObservable(
                                item.id(),
                                item.name(),
                                item.area(),
                                item.price(),
                                item.image_url(), item.capacity(),
                                item.number_of_beds(),
                                item_created_at,
                                item_updated_at,
                                item.surcharge_percentage()
                        );
                        if (roomKindObservables != null) {
                            roomKindObservables.add(roomKindObservable);
                        }
                    });
                    if (roomKindObservables != null) {
                        modelState.postValue(roomKindObservables);
                        notifySubscriptionObservers();
                    }
                }
                if (response.getErrors() != null) {
                    response.getErrors().forEach(error -> Log.e("RoomKindViewModel Subscription Error", error.toString()));
                }
            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                e.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Log.i("RoomKindViewModel Subscription Info", "Subscription Completed");
            }

            @Override
            public void onTerminated() {
                Log.i("RoomKindViewModel Subscription Info", "Subscription Terminated");
            }

            @Override
            public void onConnected() {
                Log.i("RoomKindViewModel Subscription Info", "Subscription Connected");
            }
        });
    }

}
