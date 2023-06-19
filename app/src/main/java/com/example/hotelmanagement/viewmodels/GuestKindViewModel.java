package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestKindSubscription;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.observables.GuestKindObservable;

import java.time.LocalDateTime;
import java.util.List;

public class GuestKindViewModel extends ExtendedViewModel<GuestKindObservable> {

    public GuestKindViewModel() {
        super();
    }

    @Override
    public void startSubscription() {
        Hasura.apolloClient.subscribe(new GuestKindSubscription()).execute(new ApolloSubscriptionCall.Callback<GuestKindSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<GuestKindSubscription.Data> response) {
                if (response.getData() != null) {
                    List<GuestKindObservable> guestKindObservables = modelState.getValue();
                    if (guestKindObservables != null) {
                        guestKindObservables.clear();
                    }
                    response.getData().GUESTKIND().forEach(item -> {
                        LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                        LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                        GuestKindObservable guestKindObservable = new GuestKindObservable(
                                item.id(),
                                item.name(),
                                item_created_at,
                                item_updated_at
                        );
                        if (guestKindObservables != null) {
                            guestKindObservables.add(guestKindObservable);
                        }
                    });
                    if (guestKindObservables != null) {
                        modelState.postValue(guestKindObservables);
                        notifySubscriptionObservers();
                    }
                }
                if (response.getErrors() != null) {
                    response.getErrors().forEach(error -> Log.e("GuestKindViewModel Subscription Error", error.toString()));
                }
            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                e.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Log.i("GuestKindViewModel Subscription Info", "Subscription Completed");
            }

            @Override
            public void onTerminated() {
                Log.i("GuestKindViewModel Subscription Info", "Subscription Terminated");
            }

            @Override
            public void onConnected() {
                Log.i("GuestKindViewModel Subscription Info", "Subscription Connected");
            }
        });
    }

}
