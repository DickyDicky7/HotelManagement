package com.example.hotelmanagement.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.ViewModelProvider;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Input;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.GuestByIdQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalFormByIdQuery;
import com.example.hasura.RentalFormInsertMutation;
import com.example.hasura.RentalFormSubscription;
import com.example.hasura.RentalFormUpdateByIdMutation;
import com.example.hasura.RoomPriceByIdQuery;
import com.example.hasura.RoomUpdateIsOccupiedByIdMutation;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.observables.RoomKindObservable;

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
                            response.getData().GUEST().forEach(item -> {
                                rentalFormObservable.setName(item.name());
                                rentalFormObservable.setGuestId(item.id());
                            });
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
                            response.getData().ROOM().forEach(item -> {
                                if (item.ROOMKIND() != null) {
                                    System.out.println(item.ROOMKIND().capacity());
                                    System.out.println(item.ROOMKIND().price());
                                    System.out.println(item.ROOMKIND().surcharge_percentage());
                                    Integer sub = rentalFormObservable.getNumberOfGuests() - item.ROOMKIND().capacity();
                                    if (sub < 0)
                                        sub = 0;
                                    Double pricePerDay = item.ROOMKIND().price() + item.ROOMKIND().surcharge_percentage() / 100 * item.ROOMKIND().price() * sub;
                                    rentalFormObservable.setPricePerDay(pricePerDay);
                                    rentalFormObservable.notifyPropertyChanged(BR.pricePerDayString);
                                }
                            });
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
                            RoomUpdateIsOccupiedByIdMutation roomUpdateIsOccupiedByIdMutation =
                                    RoomUpdateIsOccupiedByIdMutation.builder()
                                            .id(response.getData().insert_RENTALFORM().returning().get(0).room_id())
                                            .isOccupied(true)
                                            .build();
                            Hasura.apolloClient.mutate(roomUpdateIsOccupiedByIdMutation).enqueue(new ApolloCall.Callback<RoomUpdateIsOccupiedByIdMutation.Data>() {
                                @Override
                                public void onResponse(@NonNull Response<RoomUpdateIsOccupiedByIdMutation.Data> response) {

                                }

                                @Override
                                public void onFailure(@NonNull ApolloException e) {

                                }
                            });
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            Log.d("RentalFormViewModel Insert Response Debug", response.getData().insert_RENTALFORM().toString());
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
    public void update(RentalFormObservable rentalFormObservable,RentalFormObservable rentalFormObservable1, int id){
        RentalFormUpdateByIdMutation rentalFormUpdateByIdMutation = RentalFormUpdateByIdMutation
                .builder()
                .id(id)
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
        Hasura.apolloClient.mutate(rentalFormUpdateByIdMutation)
                .enqueue(new ApolloCall.Callback<RentalFormUpdateByIdMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormUpdateByIdMutation.Data> response) {
                        if (response.getData() != null) {
                            if (onSuccessCallback != null) {
                                onSuccessCallback.run();
                                onSuccessCallback = null;
                            }
                            List<RentalFormObservable> temp = modelState.getValue();

                            for (int j =0; j< temp.size(); j++) {
                                if (id == temp.get(j).getId()) temp.set(j, rentalFormObservable);
                            }
                            modelState.postValue(temp);
                            Log.d("RentalViewModel Update Response Debug", response.getData().update_RENTALFORM().toString());
                            if (rentalFormObservable.getRoomId() != rentalFormObservable1.getRoomId()){
                                RoomUpdateIsOccupiedByIdMutation roomUpdateIsOccupiedByIdMutation = RoomUpdateIsOccupiedByIdMutation
                                        .builder()
                                        .id(rentalFormObservable1.getRoomId())
                                        .isOccupied(false)
                                        .build();
                                Hasura.apolloClient.mutate(roomUpdateIsOccupiedByIdMutation).enqueue(new ApolloCall.Callback<RoomUpdateIsOccupiedByIdMutation.Data>() {
                                    @Override
                                    public void onResponse(@NonNull Response<RoomUpdateIsOccupiedByIdMutation.Data> response) {
                                        RoomUpdateIsOccupiedByIdMutation roomUpdateIsOccupiedByIdMutation1 = RoomUpdateIsOccupiedByIdMutation
                                                .builder()
                                                .id(rentalFormObservable.getRoomId())
                                                .isOccupied(true)
                                                .build();
                                        Hasura.apolloClient.mutate(roomUpdateIsOccupiedByIdMutation1).enqueue(new ApolloCall.Callback<RoomUpdateIsOccupiedByIdMutation.Data>() {
                                            @Override
                                            public void onResponse(@NonNull Response<RoomUpdateIsOccupiedByIdMutation.Data> response) {

                                            }

                                            @Override
                                            public void onFailure(@NonNull ApolloException e) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(@NonNull ApolloException e) {

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

                    }
                });
    }
    public void filldata(RentalFormObservable rentalFormObservable,RentalFormObservable rentalFormObservable1, int id){
        Hasura.apolloClient.query(new RentalFormByIdQuery( new Input<Integer>(id, true)))
                .enqueue(new ApolloCall.Callback<RentalFormByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            response.getData().RENTALFORM().forEach(item -> {
                                LocalDate item_start_date = item.start_date() != null ? LocalDate.parse(item.start_date().toString()) : null;
                                LocalDateTime item_created_at = item.created_at() != null ? LocalDateTime.parse(item.created_at().toString()) : null;
                                LocalDateTime item_updated_at = item.updated_at() != null ? LocalDateTime.parse(item.updated_at().toString()) : null;
                                rentalFormObservable.setId(item.id());
                                rentalFormObservable.setAmount(item.amount());
                                rentalFormObservable1.setAmount(item.amount());
                                rentalFormObservable.setRoomId(item.room_id());
                                rentalFormObservable1.setRoomId(item.room_id());
                                rentalFormObservable.setBillId(item.bill_id());
                                rentalFormObservable.setGuestId(item.guest_id());
                                rentalFormObservable.setRentalDays(item.rental_days());
                                rentalFormObservable.setIsResolved(item.is_resolved());
                                rentalFormObservable.setStartDate(item_start_date);
                                rentalFormObservable.setPricePerDay(item.price_per_day());
                                rentalFormObservable.setNumberOfGuests(item.number_of_guests());
                                rentalFormObservable.setCreatedAt(item_created_at);
                                rentalFormObservable.setUpdatedAt(item_updated_at);

                            });

                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("RentalFormViewModel Query By Id Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {

                    }
                });
    }

    public void GuestQueryById(RentalFormObservable rentalFormObservable){
        Hasura.apolloClient.query(new GuestByIdQuery(new Input<Integer>(rentalFormObservable.getGuestId(), true)))
                .enqueue(new ApolloCall.Callback<GuestByIdQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<GuestByIdQuery.Data> response) {
                        if (response.getData() != null) {
                            response.getData().GUEST().forEach(item -> {
                                rentalFormObservable.setName(item.name());
                                rentalFormObservable.setIdNumber(item.id_number());
                            });

                        }
                        if (response.getErrors() != null) {
                            response.getErrors().forEach(error -> Log.e("GuestViewModel Query By Id Error", error.toString()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull ApolloException e) {

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
