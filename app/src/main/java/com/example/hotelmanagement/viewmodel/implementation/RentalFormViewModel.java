package com.example.hotelmanagement.viewmodel.implementation;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.GuestByIdQuery;
import com.example.hasura.RentalFormDeleteByIdMutation;
import com.example.hasura.RentalFormInsertMutation;
import com.example.hasura.RentalFormSubscription;
import com.example.hasura.RentalFormUpdateByIdMutation;
import com.example.hasura.RoomPriceByIdQuery;
import com.example.hotelmanagement.hasura.Hasura;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.viewmodel.abstraction.ExtendedViewModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;

public class RentalFormViewModel extends ExtendedViewModel<RentalFormObservable> {

    public RentalFormViewModel() {
        super();
    }

    public void findGuestByGuestIdNumber(@NonNull RentalFormObservable rentalFormObservable) {

        Runnable onFailureCallback = () -> {
            rentalFormObservable.setName(null);
            rentalFormObservable.setGuestId(null);
        };
        Consumer<GuestByIdNumberQuery.GUEST> onSuccessCallback = guest -> {
            rentalFormObservable.setName(guest.name());
            rentalFormObservable.setGuestId(guest.id());
        };

        this.findGuestByGuestIdNumber(rentalFormObservable.getIdNumber(), onSuccessCallback, onFailureCallback);

    }

    public void findRentalFormPricePerDayByRoomId(RentalFormObservable rentalFormObservable) {
        RoomPriceByIdQuery roomPriceByIdQuery = RoomPriceByIdQuery
                .builder()
                .id(rentalFormObservable.getRoomId())
                .build();
        Hasura.requireInstance().requireApolloClient().query(roomPriceByIdQuery)
                .enqueue(new ApolloCall.Callback<RoomPriceByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomPriceByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            RoomPriceByIdQuery.ROOM_by_pk room_by_pk = response.getData().ROOM_by_pk();
                            if (room_by_pk != null) {
                                RoomPriceByIdQuery.ROOMKIND room_kind = room_by_pk.ROOMKIND();
                                double price = room_kind.price();
                                double surchargePercentage = room_kind.surcharge_percentage();
                                Integer capacity = room_kind.capacity();
                                Integer numberOfGuests = rentalFormObservable.getNumberOfGuests();
                                if (numberOfGuests != null) {
                                    int differences = numberOfGuests - capacity;
                                    if (differences < 0) {
                                        differences = 0;
                                    }
                                    double pricePerDay = price + surchargePercentage / 100 * price * differences;
                                    rentalFormObservable.setPricePerDay(pricePerDay);
                                    rentalFormObservable.notifyPropertyChanged(BR.pricePerDayString);
                                    Log.d("RentalFormViewModel PricePerDay Calculated Elements Debug"
                                            , "Price: " + price + ", Capacity: " + capacity
                                                    + ", SurchargePercentage: " + surchargePercentage + ", NumberOfGuests: " + numberOfGuests);
                                }
                                Log.d("RentalFormViewModel Find RentalForm PricePerDay By RoomId Response Debug", room_by_pk.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Find RentalForm PricePerDay By RoomId Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public void insert(@NonNull RentalFormObservable rentalFormObservable) {
        RentalFormInsertMutation rentalFormInsertMutation = RentalFormInsertMutation
                .builder()
                .roomId(rentalFormObservable.getRoomId())
                .billId(rentalFormObservable.getBillId())
                .amount(rentalFormObservable.getAmount())
                .guestId(rentalFormObservable.getGuestId())
                .startDate(rentalFormObservable.getStartDate())
                .rentalDays(rentalFormObservable.getRentalDays())
                .isResolved(rentalFormObservable.getIsResolved())
                .pricePerDay(rentalFormObservable.getPricePerDay())
                .numberOfGuests(rentalFormObservable.getNumberOfGuests())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(rentalFormInsertMutation)
                .enqueue(new ApolloCall.Callback<RentalFormInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            RentalFormInsertMutation.Insert_RENTALFORM_one insert_rental_form_one = response.getData().insert_RENTALFORM_one();
                            if (insert_rental_form_one != null) {
                                Log.d("RentalFormViewModel Insert Response Debug", insert_rental_form_one.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Insert Error", error.toString()));
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
    public void update(@NonNull RentalFormObservable usedRentalFormObservable, @NonNull RentalFormObservable copyRentalFormObservable) {
        RentalFormUpdateByIdMutation rentalFormUpdateByIdMutation = RentalFormUpdateByIdMutation
                .builder()
                .id(usedRentalFormObservable.getId())
                .roomId(usedRentalFormObservable.getRoomId())
                .billId(usedRentalFormObservable.getBillId())
                .amount(usedRentalFormObservable.getAmount())
                .guestId(usedRentalFormObservable.getGuestId())
                .startDate(usedRentalFormObservable.getStartDate())
                .rentalDays(usedRentalFormObservable.getRentalDays())
                .isResolved(usedRentalFormObservable.getIsResolved())
                .pricePerDay(usedRentalFormObservable.getPricePerDay())
                .numberOfGuests(usedRentalFormObservable.getNumberOfGuests())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(rentalFormUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<RentalFormUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            RentalFormUpdateByIdMutation.Update_RENTALFORM_by_pk update_rental_form_by_pk = response.getData().update_RENTALFORM_by_pk();
                            if (update_rental_form_by_pk != null) {
                                Log.d("RentalFormViewModel Update Response Debug", update_rental_form_by_pk.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Update Error", error.toString()));
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
    public void delete(@NonNull RentalFormObservable rentalFormObservable) {
        RentalFormDeleteByIdMutation rentalFormDeleteByIdMutation = RentalFormDeleteByIdMutation
                .builder()
                .id(rentalFormObservable.getId())
                .build();
        Hasura.requireInstance().requireApolloClient().mutate(rentalFormDeleteByIdMutation)
                .enqueue(new ApolloCall.Callback<RentalFormDeleteByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormDeleteByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            onSuccessHandler();
                            RentalFormDeleteByIdMutation.Delete_RENTALFORM_by_pk delete_rental_form_by_pk = response.getData().delete_RENTALFORM_by_pk();
                            if (delete_rental_form_by_pk != null) {
                                Log.d("RentalViewModel Delete Response Debug", delete_rental_form_by_pk.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            on3ErrorsHandler(response.getErrors(), null, null);
                            onFailureHandler();
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Delete Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        onFailureHandler();
                        e.printStackTrace();
                    }
                });
    }

    public void findGuestByGuestId(@NonNull RentalFormObservable rentalFormObservable) {

        Runnable onFailureCallback = () -> {
            rentalFormObservable.setName("");
            rentalFormObservable.setIdNumber("");
        };
        Consumer<GuestByIdQuery.GUEST_by_pk> onSuccessCallback = guest_by_pk -> {
            rentalFormObservable.setName(guest_by_pk.name());
            rentalFormObservable.setIdNumber(guest_by_pk.id_number());
        };

        this.findGuestByGuestId(rentalFormObservable.getGuestId(), onSuccessCallback, onFailureCallback);

    }

    @Override
    public void startSubscription() {
        Hasura.requireInstance().requireApolloClient().subscribe(new RentalFormSubscription()).execute(new ApolloSubscriptionCall.Callback<RentalFormSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<RentalFormSubscription.Data> response) {
                if (response.getData() != null) {
                    List<RentalFormObservable> rentalFormObservables = modelState.getValue();
                    if (rentalFormObservables != null) {
                        rentalFormObservables.clear();
                    }
                    response.getData().RENTALFORM().forEach(item -> {
                        RentalFormObservable rentalFormObservable = new RentalFormObservable(
                                item.id(),
                                item.amount(),
                                item.room_id(),
                                item.bill_id(),
                                item.guest_id(),
                                item.rental_days(),
                                item.is_resolved(),
                                item.price_per_day(),
                                LocalDate.parse(item.start_date().toString()),
                                item.number_of_guests(),
                                LocalDateTime.parse(item.created_at().toString()),
                                LocalDateTime.parse(item.updated_at().toString())
                        );
                        if (rentalFormObservables != null) {
                            rentalFormObservables.add(rentalFormObservable);
                        }
                    });
                    if (rentalFormObservables != null) {
                        modelState.postValue(rentalFormObservables);
                        notifySubscriptionObservers();
                    }
                }
                if (response.getErrors() != null) {
                    response.getErrors().forEach(error -> Log.e("RentalFormViewModel Subscription Error", error.toString()));
                }
            }

            @Override
            public void onFailure(@NonNull ApolloException e) {
                e.printStackTrace();
            }

            @Override
            public void onCompleted() {
                Log.i("RentalFormViewModel Subscription Info", "Subscription Completed");
            }

            @Override
            public void onTerminated() {
                Log.i("RentalFormViewModel Subscription Info", "Subscription Terminated");
            }

            @Override
            public void onConnected() {
                Log.i("RentalFormViewModel Subscription Info", "Subscription Connected");
            }
        });
    }

}
