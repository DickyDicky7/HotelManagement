package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestAllQuery;
import com.example.hasura.GuestInsertMutation;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.observables.GuestObservable;

import java.sql.Timestamp;
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
                            List<GuestObservable> guestObservables = modelState.getValue();
                            if (guestObservables != null) {
                                guestObservables.add(guestObservable);
                            }
                            modelState.postValue(guestObservables);
                            if (onSuccessCallback != null) {
                                onSuccessCallback.accept(null);
                                onSuccessCallback = null;
                            }
                            System.out.println(response.getData().insert_GUEST());
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
    public void loadData() {
        Hasura.apolloClient.query(new GuestAllQuery())
                .enqueue(new ApolloCall.Callback<GuestAllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestAllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<GuestObservable> guestObservables = modelState.getValue();
                            response.getData().GUEST().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                GuestObservable guestObservable = new GuestObservable(
                                        item.id(),
                                        item.name(),
                                        item.address(),
                                        item.id_number(),
                                        item.phone_number(),
                                        item.guestkind_id(),
                                        createdAt,
                                        updatedAt
                                );
                                if (guestObservables != null) {
                                    guestObservables.add(guestObservable);
                                }
                            });
                            modelState.postValue(guestObservables);
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
