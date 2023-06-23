package com.example.hotelmanagement.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hasura.Hasura;
import com.example.hasura.RoomKindInsertMutation;
import com.example.hasura.RoomKindSubscription;
import com.example.hasura.RoomkindByIdQuery;
import com.example.hasura.RoomkindUpdateByIdMutation;
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

    public void update(RoomKindObservable roomKindObservable, int id) {
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
                RoomkindUpdateByIdMutation roomkind_update_by_id_mutation = RoomkindUpdateByIdMutation
                        .builder()
                        .id(id)
                        .name(roomKindObservable.getName())
                        .price(roomKindObservable.getPrice())
                        .area(roomKindObservable.getArea())
                        .capacity(roomKindObservable.getCapacity())
                        .numberOfBeds(roomKindObservable.getNumberOfBeds())
                        .surchargePercentage(roomKindObservable.getSurchargePercentage())
                        .imageUrl(roomKindObservable.getImageURL())
                        .build();
                Hasura.apolloClient.mutate(roomkind_update_by_id_mutation)
                        .enqueue(new ApolloCall.Callback<RoomkindUpdateByIdMutation.Data>() {
                            @Override
                            public void onResponse(@NonNull Response<RoomkindUpdateByIdMutation.Data> response) {
                                if (response.getData() != null) {
                                    if (onSuccessCallback != null) {
                                        onSuccessCallback.run();
                                        onSuccessCallback = null;
                                    }
                                    List<RoomKindObservable> temp = modelState.getValue();

                                    for (int j = 0; j < temp.size(); j++) {
                                        if (id == temp.get(j).getId())
                                            temp.set(j, roomKindObservable);
                                    }
                                    modelState.postValue(temp);
                                    Log.d("RoomKindViewModel Update Response Debug", response.getData().update_ROOMKIND().toString());
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

    public void filldata(RoomKindObservable roomKindObservable, int id) {
        Hasura.apolloClient.query(new RoomkindByIdQuery(new Input<Integer>(id, true)))
                .enqueue(new ApolloCall.Callback<RoomkindByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomkindByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            response.getData().ROOMKIND().forEach(item -> {
                                LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                                LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                                roomKindObservable.setId(id);
                                roomKindObservable.setName(item.name());
                                roomKindObservable.setArea(item.area());
                                roomKindObservable.setPrice(item.price());
                                roomKindObservable.setCapacity(item.capacity());
                                roomKindObservable.setNumberOfBeds(item.number_of_beds());
                                roomKindObservable.setCreatedAt(item_created_at);
                                roomKindObservable.setUpdatedAt(item_updated_at);
                                roomKindObservable.setImageURL(item.image_url());
                                roomKindObservable.setSurchargePercentage(item.surcharge_percentage());

                            });

                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("RoomKindViewModel Query By Id Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {

                    }
                });
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
