package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestKindAllQuery;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.observables.GuestKindObservable;

import java.sql.Timestamp;
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
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                GuestKindObservable guestKindObservable = new GuestKindObservable(
                                        item.id(),
                                        item.name(),
                                        createdAt,
                                        updatedAt);
                                if (guestKindObservables != null) {
                                    guestKindObservables.add(guestKindObservable);
                                }
                            });
                            modelState.postValue(guestKindObservables);
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
