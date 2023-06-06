package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.BillAllQuery;
import com.example.hasura.BillInsertMutation;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalFormAmountByGuestIdQuery;
import com.example.hotelmanagement.observables.BillObservable;

import java.sql.Timestamp;
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
                            System.out.println(response.getErrors());
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
                                sum.set(sum.get() + item.amount());
                            });
                            billObservable.setCost(sum.get());
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

    public void insert(BillObservable billObservable) {
        BillInsertMutation billInsertMutation = BillInsertMutation
                .builder()
                .guestId(billObservable.getGuestId())
                .cost(billObservable.getCost())
                .isPaid(billObservable.getIsPaid())
                .build();
        Hasura.apolloClient.mutate(billInsertMutation)
                .enqueue(new ApolloCall.Callback<BillInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<BillObservable> temp = modelState.getValue();
                            temp.add(billObservable);
                            modelState.postValue(temp);


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

    @Override
    public void loadData() {
        // Call Hasura to query all the data
        Hasura.apolloClient.query(new BillAllQuery())
                .enqueue(new ApolloCall.Callback<BillAllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillAllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<BillObservable> billObservables = modelState.getValue();
                            response.getData().BILL().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;

                                BillObservable temp = new BillObservable(
                                        item.id(),
                                        item.cost(),
                                        item.is_paid(),
                                        item.guest_id(),
                                        createdAt,
                                        updatedAt
                                );
                                billObservables.add(temp);
                            });
                            modelState.postValue(billObservables);
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
