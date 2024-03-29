package com.example.hotelmanagement.viewmodel.implementation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.BillDeleteByIdMutation;
import com.example.hasura.BillInsertMutation;
import com.example.hasura.BillSubscription;
import com.example.hasura.BillUpdateByIdMutation;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.GuestByIdQuery;
import com.example.hasura.RentalFormAmountByGuestIdAndIsResolvedFalseQuery;
import com.example.hotelmanagement.hasura.Hasura;
import com.example.hotelmanagement.observable.implementation.BillObservable;
import com.example.hotelmanagement.viewmodel.abstraction.ExtendedViewModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class BillViewModel extends ExtendedViewModel<BillObservable> {

    public BillViewModel() {
        super();
    }

    public void findGuestByGuestIdNumber(@NonNull BillObservable billObservable) {

        Runnable onFailureCallback = () -> {
            billObservable.setName(null);
            billObservable.setGuestId(null);
        };
        Consumer<GuestByIdNumberQuery.GUEST> onSuccessCallback = guest -> {
            billObservable.setName(guest.name());
            billObservable.setGuestId(guest.id());
        };

        this.findGuestByGuestIdNumber(billObservable.getIdNumber(), onSuccessCallback, onFailureCallback);

    }

    public void findBillCostByGuestIdAndRentalFormIsResolvedFalse(BillObservable billObservable) {
        RentalFormAmountByGuestIdAndIsResolvedFalseQuery rentalFormAmountByGuestIdAndIsResolvedFalseQuery = RentalFormAmountByGuestIdAndIsResolvedFalseQuery
                .builder()
                .guestId(billObservable.getGuestId())
                .build();
        Hasura.requireInstance().requireApolloClient().query(rentalFormAmountByGuestIdAndIsResolvedFalseQuery)
                .enqueue(new ApolloCall.Callback<RentalFormAmountByGuestIdAndIsResolvedFalseQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormAmountByGuestIdAndIsResolvedFalseQuery.Data> response) {
                        if (response.getData() != null) {
                            AtomicReference<Double> sum = new AtomicReference<Double>(0.0);
                            response.getData().RENTALFORM().forEach(item -> sum.set(sum.get() + item.amount()));
                            billObservable.setCost(sum.get());
                            billObservable.notifyPropertyChanged(BR.costString);
                            Log.d("BillViewModel Find Bill Cost By GuestId And RentalForm IsResolved False Response Debug", response.getData().RENTALFORM().toString());
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("BillViewModel Find Bill Cost By GuestId And RentalForm IsResolved False Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void insert(@NonNull BillObservable billObservable) {
        BillInsertMutation billInsertMutation = BillInsertMutation
                .builder()
                .cost(billObservable.getCost())
                .isPaid(billObservable.getIsPaid())
                .guestId(billObservable.getGuestId())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(billInsertMutation)
                .enqueue(new ApolloCall.Callback<BillInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            BillInsertMutation.Insert_BILL_one insert_bill_one = response.getData().insert_BILL_one();
                            if (insert_bill_one != null) {
                                Log.d("BillViewModel Insert Response Debug", insert_bill_one.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("BillViewModel Insert Error", error.toString()));
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
    public void update(@NonNull BillObservable usedBillObservable, @NonNull BillObservable copyBillObservable) {
        BillUpdateByIdMutation billUpdateByIdMutation = BillUpdateByIdMutation
                .builder()
                .id(usedBillObservable.getId())
                .cost(usedBillObservable.getCost())
                .isPaid(usedBillObservable.getIsPaid())
                .guestId(usedBillObservable.getGuestId())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(billUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<BillUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            BillUpdateByIdMutation.Update_BILL_by_pk update_bill_by_pk = response.getData().update_BILL_by_pk();
                            if (update_bill_by_pk != null) {
                                Log.d("BillViewModel Update Response Debug", update_bill_by_pk.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("BillViewModel Update Error", error.toString()));
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
    public void delete(@NonNull BillObservable billObservable) {
        BillDeleteByIdMutation billDeleteByIdMutation = BillDeleteByIdMutation
                .builder()
                .id(billObservable.getId())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(billDeleteByIdMutation)
                .enqueue(new ApolloCall.Callback<BillDeleteByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillDeleteByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            BillDeleteByIdMutation.Delete_BILL_by_pk delete_bill_by_pk = response.getData().delete_BILL_by_pk();
                            if (delete_bill_by_pk != null) {
                                Log.d("BillViewModel Delete Response Debug", delete_bill_by_pk.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("BillViewModel Delete Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        onFailureHandler();
                        e.printStackTrace();
                    }
                });
    }

    public void findGuestByGuestId(@NonNull BillObservable billObservable) {

        Runnable onFailureCallback = () -> {
            billObservable.setName("");
            billObservable.setIdNumber("");
        };
        Consumer<GuestByIdQuery.GUEST_by_pk> onSuccessCallback = guest_by_pk -> {
            billObservable.setName(guest_by_pk.name());
            billObservable.setIdNumber(guest_by_pk.id_number());
        };

        this.findGuestByGuestId(billObservable.getGuestId(), onSuccessCallback, onFailureCallback);

    }

    @Override
    public void startSubscription() {
        Hasura.requireInstance().requireApolloClient().subscribe(new BillSubscription()).execute(new ApolloSubscriptionCall.Callback<BillSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<BillSubscription.Data> response) {
                if (response.getData() != null) {
                    List<BillObservable> billObservables = modelState.getValue();
                    if (billObservables != null) {
                        billObservables.clear();
                    }
                    response.getData().BILL().forEach(item -> {
                        BillObservable billObservable = new BillObservable(
                                item.id(),
                                item.cost(),
                                item.is_paid(),
                                item.guest_id(),
                                LocalDateTime.parse(item.created_at().toString()),
                                LocalDateTime.parse(item.updated_at().toString())
                        );
                        if (billObservables != null) {
                            billObservables.add(billObservable);
                        }
                    });
                    if (billObservables != null) {
                        modelState.postValue(billObservables);
                        notifySubscriptionObservers();
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
