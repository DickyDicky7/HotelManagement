package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestByIdQuery;
import com.example.hasura.GuestInsertMutation;
import com.example.hasura.GuestSubscription;
import com.example.hasura.GuestUpdateByIdMutation;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.observables.GuestObservable;

import java.time.LocalDateTime;
import java.util.List;

public class GuestViewModel extends ExtendedViewModel<GuestObservable> {

    public GuestViewModel() {
        super();
    }

    public void insert(GuestObservable guestObservable) {
        GuestInsertMutation guestInsertMutation = GuestInsertMutation
                .builder()
                .name(guestObservable.getName())
                .address(guestObservable.getAddress())
                .idNumber(guestObservable.getIdNumber())
                .guestKindId(guestObservable.getGuestKindId())
                .phoneNumber(guestObservable.getPhoneNumber())
                .build();
        Hasura.apolloClient.mutate(guestInsertMutation)
                .enqueue(new ApolloCall.Callback<GuestInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            Log.d("GuestViewModel Insert Response Debug", response.getData().insert_GUEST().toString());
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("GuestViewModel Insert Error", error.toString()));
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

    public void update (GuestObservable guestObservable , int id){
        GuestUpdateByIdMutation guest_update_by_id_mutation = GuestUpdateByIdMutation
                .builder()
                .id(id)
                .name(guestObservable.getName())
                .address(guestObservable.getAddress())
                .idNumber(guestObservable.getIdNumber())
                .guestKindId(guestObservable.getGuestKindId())
                .phoneNumber(guestObservable.getPhoneNumber())
                .build();
        Hasura.apolloClient.mutate(guest_update_by_id_mutation)
                .enqueue(new ApolloCall.Callback<GuestUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            List<GuestObservable> temp = modelState.getValue();

                            for (int j = 0; j< temp.size(); j++) {
                                if (id == temp.get(j).getId()) temp.set(j, guestObservable);
                            }
                            modelState.postValue(temp);

                            System.out.println(response.getData().update_GUEST());
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("GuestViewModel Update Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {

                    }
                });
    }

    public void filldata(GuestObservable guestObservable,int id){
        Hasura.apolloClient.query(new GuestByIdQuery(new Input<Integer>(id, true)))
                .enqueue(new ApolloCall.Callback<GuestByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            response.getData().GUEST().forEach(item -> {
                                LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                                LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                                guestObservable.setId(item.id());
                                guestObservable.setName(item.name());
                                guestObservable.setAddress(item.address());
                                guestObservable.setIdNumber(item.id_number());
                                guestObservable.setPhoneNumber(item.phone_number());
                                guestObservable.setGuestKindId(item.guestkind_id());
                                guestObservable.setCreatedAt(item_created_at);
                                guestObservable.setUpdatedAt(item_updated_at);
                            });

                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("GuestViewModel Query By Id Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {

                    }
                });
    }

    @Override
    public void startSubscription() {
        Hasura.apolloClient.subscribe(new GuestSubscription()).execute(new ApolloSubscriptionCall.Callback<GuestSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<GuestSubscription.Data> response) {
                if (response.getData() != null) {
                    List<GuestObservable> guestObservables = modelState.getValue();
                    if (guestObservables != null) {
                        guestObservables.clear();
                    }
                    response.getData().GUEST().forEach(item -> {
                        LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                        LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                        GuestObservable guestObservable = new GuestObservable(
                                item.id(),
                                item.name(),
                                item.address(),
                                item.id_number(),
                                item.phone_number(),
                                item.guestkind_id(),
                                item_created_at,
                                item_updated_at
                        );
                        if (guestObservables != null) {
                            guestObservables.add(guestObservable);
                        }
                    });
                    if (guestObservables != null) {
                        modelState.postValue(guestObservables);
                    }
                }
                if (response.getErrors() != null) {
                    response.getErrors().forEach(error -> Log.e("GuestViewModel Subscription Error", error.toString()));
                }
            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                e.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Log.i("GuestViewModel Subscription Info", "Subscription Completed");
            }

            @Override
            public void onTerminated() {
                Log.i("GuestViewModel Subscription Info", "Subscription Terminated");
            }

            @Override
            public void onConnected() {
                Log.i("GuestViewModel Subscription Info", "Subscription Connected");
            }
        });
    }

}
