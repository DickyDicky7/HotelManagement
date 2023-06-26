package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.BillInsertMutation;
import com.example.hasura.BillSubscription;
import com.example.hasura.BillUpdateByIdMutation;
import com.example.hasura.BillUpdateCostByIdMutation;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.GuestByIdQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalFormAmountByGuestIdQuery;
import com.example.hasura.RentalFormUpdateBillIdAndIsResolvedByIdMutation;
import com.example.hasura.RoomUpdateIsOccupiedByIdMutation;
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
                            GuestByIdNumberQuery.GUEST guest = response.getData().GUEST().get(0);
                            billObservable.setName(guest.name());
                            billObservable.setGuestId(guest.id());
                            Log.d("BillViewModel Find GuestId Response Debug", guest.toString());
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
                            AtomicReference<Double> sum = new AtomicReference<Double>(0.0);
                            response.getData().RENTALFORM().forEach(item -> {
                                Double amount = item.amount();
                                if (amount != null) {
                                    sum.set(sum.get() + amount);
                                }
                            });
                            billObservable.setCost(sum.get());
                            billObservable.notifyPropertyChanged(BR.costString);
                            Log.d("BillViewModel Calculate Cost Response Debug", response.getData().RENTALFORM().toString());
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
                            BillInsertMutation.Insert_BILL insert_bill = response.getData().insert_BILL();
                            if (insert_bill != null) {
                                RentalFormUpdateBillIdAndIsResolvedByIdMutation rentalFormUpdateBillIdAndIsResolvedByIdMutation =
                                        RentalFormUpdateBillIdAndIsResolvedByIdMutation
                                                .builder()
                                                .isResolved(true)
                                                .billId(insert_bill.returning().get(0).id())
                                                .guestId(insert_bill.returning().get(0).guest_id())
                                                .build();
                                Hasura.apolloClient.mutate(rentalFormUpdateBillIdAndIsResolvedByIdMutation)
                                        .enqueue(new ApolloCall.Callback<RentalFormUpdateBillIdAndIsResolvedByIdMutation.Data>() {
                                            @Override
                                            public void onResponse(@NonNull Response<RentalFormUpdateBillIdAndIsResolvedByIdMutation.Data> response) {
                                                if (response.getData() != null) {
                                                    RentalFormUpdateBillIdAndIsResolvedByIdMutation.Update_RENTALFORM update_rentalform = response.getData().update_RENTALFORM();
                                                    if (update_rentalform != null) {
                                                        update_rentalform.returning().forEach(item -> {
                                                            RoomUpdateIsOccupiedByIdMutation roomUpdateIsOccupiedByIdMutation
                                                                    = RoomUpdateIsOccupiedByIdMutation
                                                                    .builder()
                                                                    .isOccupied(false)
                                                                    .id(item.room_id())
                                                                    .build();
                                                            Hasura.apolloClient.mutate(roomUpdateIsOccupiedByIdMutation)
                                                                    .enqueue(new ApolloCall.Callback<RoomUpdateIsOccupiedByIdMutation.Data>() {
                                                                        @Override
                                                                        public void onResponse(@NonNull Response<RoomUpdateIsOccupiedByIdMutation.Data> response) {
                                                                            if (response.getData() != null) {
                                                                                RoomUpdateIsOccupiedByIdMutation.Update_ROOM update_room = response.getData().update_ROOM();
                                                                                if (update_room != null) {
                                                                                    Log.d("BillViewModel Room Update IsOccupied By Id Response Debug", update_room.toString());
                                                                                }
                                                                            }
                                                                            if (response.getErrors() != null) {
                                                                                response.getErrors().forEach(error -> Log.e("BillViewModel Room Update IsOccupied By Id Error", error.toString()));
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure(@NonNull ApolloException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    });
                                                        });
                                                        Log.d("BillViewModel RentalForm Update BillId And IsResolved By Id Response Debug", update_rentalform.toString());
                                                    }
                                                }
                                                if (response.getErrors() != null) {
                                                    response.getErrors().forEach(error -> Log.e("BillViewModel RentalForm Update BillId And IsResolved By Id Error", error.toString()));
                                                }
                                            }

                                            @Override
                                            public void onFailure(@NonNull ApolloException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                if (onSuccessCallback != null) {
                                    onSuccessCallback.run();
                                    onSuccessCallback = null;
                                }
                                Log.d("BillViewModel Insert Response Debug", insert_bill.toString());
                            }
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

    public void update(BillObservable usedBillObservable, BillObservable copyBillObservable) {
        BillUpdateByIdMutation billUpdateByIdMutation = BillUpdateByIdMutation
                .builder()
                .id(usedBillObservable.getId())
                .cost(usedBillObservable.getCost())
                .isPaid(usedBillObservable.getIsPaid())
                .guestId(usedBillObservable.getGuestId())
                .build();
        Hasura.apolloClient.mutate(billUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<BillUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            BillUpdateByIdMutation.Update_BILL update_bill = response.getData().update_BILL();
                            if (update_bill != null) {
                                Log.d("BillViewModel Update Response Debug", update_bill.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("BillViewModel Update Error", error.toString()));
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

    public void findGuest(BillObservable billObservable) {
        Hasura.apolloClient.query(new GuestByIdQuery(new Input<Integer>(billObservable.getGuestId(), true)))
                .enqueue(new ApolloCall.Callback<GuestByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            GuestByIdQuery.GUEST guest = response.getData().GUEST().get(0);
                            billObservable.setName(guest.name());
                            billObservable.setIdNumber(guest.id_number());
                            Log.d("BillViewModel Find Guest Response Debug", guest.toString());
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("BillViewModel Find Guest Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void updateCost(BillObservable billObservable, Double cost) {
        BillUpdateCostByIdMutation billUpdateCostByIdMutation = BillUpdateCostByIdMutation
                .builder()
                .cost(cost)
                .id(billObservable.getId())
                .build();
        Hasura.apolloClient.mutate(billUpdateCostByIdMutation)
                .enqueue(new ApolloCall.Callback<BillUpdateCostByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<BillUpdateCostByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            BillUpdateCostByIdMutation.Update_BILL update_bill = response.getData().update_BILL();
                            if (update_bill != null) {
                                Log.d("BillViewModel Update Cost Response Debug", update_bill.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("BillViewModel Update Cost Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
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
