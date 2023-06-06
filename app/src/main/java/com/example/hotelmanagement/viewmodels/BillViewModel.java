package com.example.hotelmanagement.viewmodels;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Bill_AllQuery;
import com.example.hasura.Bill_insertMutation;
import com.example.hasura.Guest_by_idNumberQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalForm_amount_by_guestIdQuery;
import com.example.hotelmanagement.ActivityMain;
import com.example.hotelmanagement.observables.BillObservable;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class BillViewModel extends ExtendedViewModel<BillObservable> {

    public BillViewModel() {
        super();
    }

    public void checkObservable(BillObservable billObservable) {
        if (billObservable.getIdNumber() == null || billObservable.getIdNumber().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Guest's information is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (billObservable.getGuestIdString() == null || billObservable.getGuestIdString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Cannot find this guest", Toast.LENGTH_SHORT).show();
            return;
        }
        insert(billObservable);
    }

    public void findGuestId(BillObservable billObservable) {
        Guest_by_idNumberQuery guestByIdNumberQuery = Guest_by_idNumberQuery
                .builder()
                .idNumber(billObservable.getIdNumber())
                .build();
        Hasura.apolloClient.query(guestByIdNumberQuery)
                .enqueue(new ApolloCall.Callback<Guest_by_idNumberQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Guest_by_idNumberQuery.Data> response) {
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
        RentalForm_amount_by_guestIdQuery rentalFormAmountByGuestIdQuery = RentalForm_amount_by_guestIdQuery
                .builder()
                .guestId(billObservable.getGuestId())
                .build();
        Hasura.apolloClient.query(rentalFormAmountByGuestIdQuery)
                .enqueue(new ApolloCall.Callback<RentalForm_amount_by_guestIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalForm_amount_by_guestIdQuery.Data> response) {
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
        Bill_insertMutation billInsertMutation = Bill_insertMutation
                .builder()
                .guestid(billObservable.getGuestId())
                .cost(billObservable.getCost())
                .isPaid(billObservable.getIsPaid())
                .build();
        Hasura.apolloClient.mutate(billInsertMutation)
                .enqueue(new ApolloCall.Callback<Bill_insertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Bill_insertMutation.Data> response) {
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
        Hasura.apolloClient.query(new Bill_AllQuery())
                .enqueue(new ApolloCall.Callback<Bill_AllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Bill_AllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<BillObservable> billObservables = modelState.getValue();
                            response.getData().BILL().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;

                                BillObservable temp = new BillObservable(
                                        item.id(),
                                        item.is_paid(),
                                        item.guest_id(),
                                        item.cost(),
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
        dataLoaded = true;
    }

}
