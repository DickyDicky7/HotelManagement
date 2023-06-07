package com.example.hotelmanagement.viewmodels;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalFormAllQuery;
import com.example.hasura.RentalFormInsertMutation;
import com.example.hasura.RoomPriceByIdQuery;
import com.example.hotelmanagement.observables.RentalFormObservable;

import java.sql.Date;
import java.sql.Timestamp;
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
                            System.out.println(response.getErrors());
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
                                    Integer sub = rentalFormObservable.getNumOfGuests() - item.ROOMKIND().capacity();
                                    if (sub < 0)
                                        sub = 0;
                                    Double pricePerDay = item.ROOMKIND().price() + item.ROOMKIND().surcharge_percentage() / 100 * item.ROOMKIND().price() * sub;
                                    rentalFormObservable.setPricePerDay(pricePerDay);
                                }
                            });
                        }
                        if (response.getErrors() != null) {
                            System.out.println((response.getErrors()));
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
                .numOfGuests(rentalFormObservable.getNumOfGuests())
                .pricePerDay(rentalFormObservable.getPricePerDay())
                .build();
        Hasura.apolloClient.mutate(rentalFormInsertMutation)
                .enqueue(new ApolloCall.Callback<RentalFormInsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormInsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<RentalFormObservable> rentalFormObservables = modelState.getValue();
                            if (rentalFormObservables != null) {
                                rentalFormObservables.add(rentalFormObservable);
                                modelState.postValue(rentalFormObservables);
                            }
                            if (onSuccessCallback != null) {
                                onSuccessCallback.accept(null);
                                onSuccessCallback = null;
                            }
                            System.out.println(response.getData().insert_RENTALFORM());
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
        Hasura.apolloClient.query(new RentalFormAllQuery())
                .enqueue(new ApolloCall.Callback<RentalFormAllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalFormAllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<RentalFormObservable> rentalFormObservables = modelState.getValue();
                            response.getData().RENTALFORM().forEach(item -> {
                                Date startDate = item.start_date() != null ? Date.valueOf(item.start_date().toString()) : null;
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                RentalFormObservable rentalFormObservable = new RentalFormObservable(
                                        item.id(),
                                        item.amount(),
                                        startDate,
                                        item.room_id(),
                                        item.bill_id(),
                                        item.guest_id(),
                                        item.rental_days(),
                                        item.is_resolved(),
                                        item.price_per_day(),
                                        item.number_of_guests(),
                                        createdAt,
                                        updatedAt
                                );
                                if (rentalFormObservables != null) {
                                    rentalFormObservables.add(rentalFormObservable);
                                }
                            });
                            modelState.postValue(rentalFormObservables);
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
