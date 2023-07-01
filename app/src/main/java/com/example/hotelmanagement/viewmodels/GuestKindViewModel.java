package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    @NonNull
    public String getGuestKindName(@Nullable Integer id) {
        GuestKindObservable guestKindObservable = getObservable(id);
        if (guestKindObservable != null && guestKindObservable.getName() != null) {
            return guestKindObservable.getName();
        }
        return "";
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
                        GuestKindObservable guestKindObservable = new GuestKindObservable(
                                item.id(),
                                item.name(),
                                item.discount_coefficient(),
                                LocalDateTime.parse(item.created_at().toString()),
                                LocalDateTime.parse(item.updated_at().toString())
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
