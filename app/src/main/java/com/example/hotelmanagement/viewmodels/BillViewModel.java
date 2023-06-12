package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.BillInsertMutation;
import com.example.hasura.BillSubscription;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalFormAmountByGuestIdQuery;
import com.example.hotelmanagement.observables.BillObservable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BillViewModel extends ExtendedViewModel<BillObservable> {

    public BillViewModel() {
        super();
    }

    public void findGuestId(BillObservable billObservable) {
        GuestByIdNumberQuery guestByIdNumberQuery = GuestByIdNumberQuery
                .builder()
                .idNumber(billObservable.getIdNumber())
                .build();
        Hasura.apolloClient.query(guestByIdNumberQuery)
                .enqueue(new ApolloCall.Callback<GuestByIdNumberQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestByIdNumberQuery.Data> response) {
                        if (response.getData() != null) {
                            response.getData().GUEST().forEach(item -> {
                                billObservable.setName(item.name());
                                billObservable.setGuestId(item.id());
                            });
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("BillViewModel Find GuestId Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void calculateCost(BillObservable billObservable) {
        RentalFormAmountByGuestIdQuery rentalFormAmountByGuestIdQuery = RentalFormAmountByGuestIdQuery
                .builder()
                .guestId(billObservable.getGuestId())
                .build();
        Hasura.apolloClient.query(rentalFormAmountByGuestIdQuery)
                .enqueue(new ApolloCall.Callback<RentalFormAmountByGuestIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormAmountByGuestIdQuery.Data> response) {
                        if (response.getData() != null) {
                            AtomicReference<Double> sum = new AtomicReference<>(0.0);
                            response.getData().RENTALFORM().forEach(item -> {
                                if (item.amount() != null) {
                                    sum.set(sum.get() + item.amount());
                                }
                            });
                            billObservable.setCost(sum.get());
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("BillViewModel Calculate Cost Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void insert(BillObservable billObservable) {
        BillInsertMutation billInsertMutation = BillInsertMutation
                .builder()
                .cost(billObservable.getCost())
                .isPaid(billObservable.getIsPaid())
                .guestId(billObservable.getGuestId())
                .build();
        Hasura.apolloClient.mutate(billInsertMutation)
                .enqueue(new ApolloCall.Callback<BillInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            Log.d("BillViewModel Insert Response Debug", response.getData().insert_BILL().toString());
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("BillViewModel Insert Error", error.toString()));
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
    public void startSubscription() {
        Hasura.apolloClient.subscribe(new BillSubscription()).execute(new ApolloSubscriptionCall.Callback<BillSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<BillSubscription.Data> response) {
                if (response.getData() != null) {
                    List<BillObservable> billObservables = modelState.getValue();
                    if (billObservables != null) {
                        billObservables.clear();
                    }
                    response.getData().BILL().forEach(item -> {
                        LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                        LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                        BillObservable billObservable = new BillObservable(
                                item.id(),
                                item.cost(),
                                item.is_paid(),
                                item.guest_id(),
                                item_created_at,
                                item_updated_at
                        );
                        if (billObservables != null) {
                            billObservables.add(billObservable);
                        }
                    });
                    if (billObservables != null) {
                        modelState.postValue(billObservables);
                    }
                }
                if (response.getErrors() != null) {
                    response.getErrors().forEach(error -> Log.e("BillViewModel Subscription Error", error.toString()));
                }
            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                e.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Log.i("BillViewModel Subscription Info", "Subscription Completed");
            }

            @Override
            public void onTerminated() {
                Log.i("BillViewModel Subscription Info", "Subscription Terminated");
            }

            @Override
            public void onConnected() {
                Log.i("BillViewModel Subscription Info", "Subscription Connected");
            }
        });
    }

}
