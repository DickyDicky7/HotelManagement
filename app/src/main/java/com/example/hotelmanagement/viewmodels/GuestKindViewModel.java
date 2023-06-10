package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestKindAllQuery;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.observables.GuestKindObservable;

import java.time.LocalDateTime;
import java.util.List;

public class GuestKindViewModel extends ExtendedViewModel<GuestKindObservable> {

    public GuestKindViewModel() {
        super();
    }

    @Override
    public void loadData() {
        Hasura.apolloClient.query(new GuestKindAllQuery())
                .enqueue(new ApolloCall.Callback<GuestKindAllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestKindAllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<GuestKindObservable> guestKindObservables = modelState.getValue();
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
