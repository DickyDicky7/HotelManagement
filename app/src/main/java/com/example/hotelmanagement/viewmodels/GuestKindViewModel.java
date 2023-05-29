package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestKind_AllQuery;
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
        // Call Hasura to query all the data
        Hasura.apolloClient.query(new GuestKind_AllQuery())
                .enqueue(new ApolloCall.Callback<GuestKind_AllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestKind_AllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<GuestKindObservable> guestKindObservables = modelState.getValue();
                            response.getData().GUESTKIND().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                GuestKindObservable temp = new GuestKindObservable(
                                        item.id(),
                                        item.name(),
                                        createdAt,
                                        updatedAt);
                                guestKindObservables.add(temp);
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
        dataLoaded = true;
    }

}
