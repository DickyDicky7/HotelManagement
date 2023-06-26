package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.GuestByIdQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalFormInsertMutation;
import com.example.hasura.RentalFormSubscription;
import com.example.hasura.RentalFormUpdateByIdMutation;
import com.example.hasura.RoomPriceByIdQuery;
import com.example.hasura.RoomUpdateIsOccupiedByIdMutation;
import com.example.hotelmanagement.observables.RentalFormObservable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RentalFormViewModel extends ExtendedViewModel<RentalFormObservable> {

    public RentalFormViewModel() {
        super();
    }

    public void findGuestId(RentalFormObservable rentalFormObservable) {
        GuestByIdNumberQuery guestByIdNumberQuery = GuestByIdNumberQuery
                .builder()
                .idNumber(rentalFormObservable.getIdNumber())
                .build();
        Hasura.apolloClient.query(guestByIdNumberQuery)
                .enqueue(new ApolloCall.Callback<GuestByIdNumberQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestByIdNumberQuery.Data> response) {
                        if (response.getData() != null) {
                            GuestByIdNumberQuery.GUEST guest = response.getData().GUEST().get(0);
                            rentalFormObservable.setName(guest.name());
                            rentalFormObservable.setGuestId(guest.id());
                            Log.d("RentalFormViewModel Find GuestId Response Debug", guest.toString());
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Find GuestId Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void findPrice(RentalFormObservable rentalFormObservable) {
        RoomPriceByIdQuery roomPriceByIdQuery = RoomPriceByIdQuery
                .builder()
                .id(rentalFormObservable.getRoomId())
                .build();
        Hasura.apolloClient.query(roomPriceByIdQuery)
                .enqueue(new ApolloCall.Callback<RoomPriceByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RoomPriceByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            RoomPriceByIdQuery.ROOM room = response.getData().ROOM().get(0);
                            RoomPriceByIdQuery.ROOMKIND roomkind = room.ROOMKIND();
                            if (roomkind != null) {
                                Double price = roomkind.price();
                                Double surchargePercentage = roomkind.surcharge_percentage();
                                Integer capacity = roomkind.capacity();
                                Integer numberOfGuests = rentalFormObservable.getNumberOfGuests();
                                if (price != null && surchargePercentage != null && capacity != null && numberOfGuests != null) {
                                    Integer differences = numberOfGuests - capacity;
                                    if (differences < 0) {
                                        differences = 0;
                                    }
                                    Double pricePerDay = price + surchargePercentage / 100 * price * differences;
                                    rentalFormObservable.setPricePerDay(pricePerDay);
                                    rentalFormObservable.notifyPropertyChanged(BR.pricePerDayString);
                                    Log.d("RoomKind Debug", "Price: " + price + ", Capacity: " + capacity + ", SurchargePercentage: " + surchargePercentage + ", NumberOfGuests: " + numberOfGuests);
                                }
                            }
                            Log.d("RentalFormViewModel Find Price Response Debug", room.toString());
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Find Price Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void insert(RentalFormObservable rentalFormObservable) {
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
        Hasura.apolloClient.mutate(rentalFormInsertMutation)
                .enqueue(new ApolloCall.Callback<RentalFormInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            RentalFormInsertMutation.Insert_RENTALFORM insert_rentalform = response.getData().insert_RENTALFORM();
                            if (insert_rentalform != null) {
                                RoomUpdateIsOccupiedByIdMutation roomUpdateIsOccupiedByIdMutation = RoomUpdateIsOccupiedByIdMutation.builder()
                                        .id(insert_rentalform.returning().get(0).room_id())
                                        .isOccupied(true)
                                        .build();
                                Hasura.apolloClient.mutate(roomUpdateIsOccupiedByIdMutation).enqueue(new ApolloCall.Callback<RoomUpdateIsOccupiedByIdMutation.Data>() {
                                    @Override
                                    public void onResponse(@NonNull Response<RoomUpdateIsOccupiedByIdMutation.Data> response) {
                                        if (response.getData() != null) {
                                            RoomUpdateIsOccupiedByIdMutation.Update_ROOM update_room = response.getData().update_ROOM();
                                            if (update_room != null) {
                                                Log.d("RentalFormViewModel Room Update IsOccupied By Id Response Debug", update_room.toString());
                                            }
                                        }
                                        if (response.getErrors() != null) {
                                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Room Update IsOccupied By Id Error", error.toString()));
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
                                Log.d("RentalFormViewModel Insert Response Debug", insert_rentalform.toString());
                            }
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Insert Error", error.toString()));
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

    public void update(RentalFormObservable usedRentalFormObservable, RentalFormObservable copyRentalFormObservable) {
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
        Hasura.apolloClient.mutate(rentalFormUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<RentalFormUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            RentalFormUpdateByIdMutation.Update_RENTALFORM update_rentalform = response.getData().update_RENTALFORM();
                            if (update_rentalform != null) {
                                Log.d("RentalViewModel Update Response Debug", update_rentalform.toString());
                            }
                            if (!usedRentalFormObservable.getRoomId().equals(copyRentalFormObservable.getRoomId())) {
                                RoomUpdateIsOccupiedByIdMutation roomUpdateIsOccupiedFByIdMutation = RoomUpdateIsOccupiedByIdMutation
                                        .builder()
                                        .id(copyRentalFormObservable.getRoomId())
                                        .isOccupied(false)
                                        .build();
                                RoomUpdateIsOccupiedByIdMutation roomUpdateIsOccupiedTByIdMutation = RoomUpdateIsOccupiedByIdMutation
                                        .builder()
                                        .id(usedRentalFormObservable.getRoomId())
                                        .isOccupied(true)
                                        .build();
                                Hasura.apolloClient.mutate(roomUpdateIsOccupiedFByIdMutation).enqueue(new ApolloCall.Callback<RoomUpdateIsOccupiedByIdMutation.Data>() {
                                    @Override
                                    public void onResponse(@NonNull Response<RoomUpdateIsOccupiedByIdMutation.Data> response) {
                                        if (response.getData() != null) {
                                            RoomUpdateIsOccupiedByIdMutation.Update_ROOM update_room = response.getData().update_ROOM();
                                            if (update_room != null) {
                                                Log.d("RentalFormViewModel Room Update IsOccupied False By Id Response Debug", update_room.toString());
                                            }
                                        }
                                        if (response.getErrors() != null) {
                                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Room Update IsOccupied False By Id Error", error.toString()));
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull ApolloException e) {
                                        e.printStackTrace();
                                    }
                                });
                                Hasura.apolloClient.mutate(roomUpdateIsOccupiedTByIdMutation).enqueue(new ApolloCall.Callback<RoomUpdateIsOccupiedByIdMutation.Data>() {
                                    @Override
                                    public void onResponse(@NonNull Response<RoomUpdateIsOccupiedByIdMutation.Data> response) {
                                        if (response.getData() != null) {
                                            RoomUpdateIsOccupiedByIdMutation.Update_ROOM update_room = response.getData().update_ROOM();
                                            if (update_room != null) {
                                                Log.d("RentalFormViewModel Room Update IsOccupied True By Id Response Debug", update_room.toString());
                                            }
                                        }
                                        if (response.getErrors() != null) {
                                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Room Update IsOccupied True By Id Error", error.toString()));
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull ApolloException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        }
                        if (response.getErrors() != null) {
                            if (onFailureCallback != null) {
                                onFailureCallback.run();
                                onFailureCallback = null;
                            }
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Update Error", error.toString()));
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

    public void findGuest(RentalFormObservable rentalFormObservable) {
        Hasura.apolloClient.query(new GuestByIdQuery(new Input<Integer>(rentalFormObservable.getGuestId(), true)))
                .enqueue(new ApolloCall.Callback<GuestByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            GuestByIdQuery.GUEST guest = response.getData().GUEST().get(0);
                            rentalFormObservable.setName(guest.name());
                            rentalFormObservable.setIdNumber(guest.id_number());
                            Log.d("RentalFormViewModel Find Guest Response Debug", guest.toString());
                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Find Guest Error", error.toString()));
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
        Hasura.apolloClient.subscribe(new RentalFormSubscription()).execute(new ApolloSubscriptionCall.Callback<RentalFormSubscription.Data>() {
            @Override
            public void onResponse(@NonNull Response<RentalFormSubscription.Data> response) {
                if (response.getData() != null) {
                    List<RentalFormObservable> rentalFormObservables = modelState.getValue();
                    if (rentalFormObservables != null) {
                        rentalFormObservables.clear();
                    }
                    response.getData().RENTALFORM().forEach(item -> {
                        LocalDate item_start_date = item.start_date() != null ? LocalDate.parse(item.start_date().toString()) : null;
                        LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                        LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                        RentalFormObservable rentalFormObservable = new RentalFormObservable(
                                item.id(),
                                item.amount(),
                                item.room_id(),
                                item.bill_id(),
                                item.guest_id(),
                                item.rental_days(),
                                item.is_resolved(),
                                item.price_per_day(),
                                item_start_date,
                                item.number_of_guests(),
                                item_created_at,
                                item_updated_at
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
