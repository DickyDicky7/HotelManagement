package com.example.hotelmanagement.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hasura.Hasura;
import com.example.hasura.RoomKindAllQuery;
import com.example.hasura.RoomKindInsertMutation;
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

            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {

            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                roomKindObservable.setImageURL(resultData.getOrDefault("url", "").toString());
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
            public void onError(String requestId, ErrorInfo error) {
                Log.e("Upload Image Error", error.toString());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {

            }
        }).dispatch();
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
                                LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                                LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                                RoomKindObservable roomKindObservable = new RoomKindObservable(
                                        item.id(),
                                        item.name(),
                                        item.area(),
                                        item.price(),
                                        item.image_url(),
                                        item.capacity(),
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
