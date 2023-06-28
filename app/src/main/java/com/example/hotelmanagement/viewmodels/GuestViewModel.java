package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
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
                            onSuccessHandler();
                            GuestInsertMutation.Insert_GUEST insert_guest = response.getData().insert_GUEST();
                            if (insert_guest != null) {
                                Log.d("GuestViewModel Insert Response Debug", insert_guest.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("GuestViewModel Insert Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        onFailureHandler();
                        e.printStackTrace();
                    }
                });
    }

    public void update(GuestObservable usedGuestObservable, GuestObservable copyGuestObservable) {
        GuestUpdateByIdMutation guestUpdateByIdMutation = GuestUpdateByIdMutation
                .builder()
                .id(usedGuestObservable.getId())
                .name(usedGuestObservable.getName())
                .address(usedGuestObservable.getAddress())
                .idNumber(usedGuestObservable.getIdNumber())
                .guestKindId(usedGuestObservable.getGuestKindId())
                .phoneNumber(usedGuestObservable.getPhoneNumber())
                .build();
        Hasura.apolloClient.mutate(guestUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<GuestUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            GuestUpdateByIdMutation.Update_GUEST update_guest = response.getData().update_GUEST();
                            if (update_guest != null) {
                                Log.d("GuestViewModel Update Response Debug", update_guest.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("GuestViewModel Update Error", error.toString()));
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
                        notifySubscriptionObservers();
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
