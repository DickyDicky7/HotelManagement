package com.example.hotelmanagement.viewmodels;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Guest_by_idNumberQuery;
import com.example.hasura.Hasura;
import com.example.hasura.RentalForm_AllQuery;
import com.example.hasura.RentalForm_insertMutation;
import com.example.hasura.Room_price_by_idQuery;
import com.example.hotelmanagement.ActivityMain;
import com.example.hotelmanagement.observables.RentalFormObservable;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class RentalFormViewModel extends ExtendedViewModel<RentalFormObservable> {
    public RentalFormViewModel() {
        super();
    }

    public void checkObservable(RentalFormObservable rentalFormObservable) {

        if (rentalFormObservable.getIdNumber() == null || rentalFormObservable.getIdNumber().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Guest's information is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rentalFormObservable.getRoomIdString() == null || rentalFormObservable.getRoomIdString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Room's id is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rentalFormObservable.getRentalDaysString() == null || rentalFormObservable.getRentalDaysString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Rental days is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rentalFormObservable.getStartDateString() == null || rentalFormObservable.getStartDateString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Start date is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rentalFormObservable.getNumOfGuestsString() == null || rentalFormObservable.getNumOfGuestsString().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Number of guests is missing", Toast.LENGTH_SHORT).show();
        }
        if (rentalFormObservable.getGuestId() == null) {
            Toast.makeText(ActivityMain.getInstance(), "Cannot find this guest", Toast.LENGTH_SHORT).show();
            return;
        }
        //set null for bill id
        rentalFormObservable.setBillId(null);
        //set false for isResolved
        rentalFormObservable.setIsResolved(false);
        //insert
        insert(rentalFormObservable);
    }

    public void findGuestId(RentalFormObservable rentalFormObservable) {
        Guest_by_idNumberQuery guestByIdNumberQuery = Guest_by_idNumberQuery
                .builder()
                .idNumber(rentalFormObservable.getIdNumber())
                .build();
        Hasura.apolloClient.query(guestByIdNumberQuery)
                .enqueue(new ApolloCall.Callback<Guest_by_idNumberQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Guest_by_idNumberQuery.Data> response) {
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
        Room_price_by_idQuery roomPriceByIdQuery = Room_price_by_idQuery
                .builder()
                .id(rentalFormObservable.getRoomId())
                .build();
        Hasura.apolloClient.query(roomPriceByIdQuery)
                .enqueue(new ApolloCall.Callback<Room_price_by_idQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Room_price_by_idQuery.Data> response) {
                        if (response.getData() != null) {
                            response.getData().ROOM().forEach(item -> {
                                Integer sub = rentalFormObservable.getNumOfGuests() - Objects.requireNonNull(item.ROOMKIND()).capacity();
                                if (sub < 0)
                                    sub = 0;
                                Double pricePerDay = item.ROOMKIND().price() +
                                        item.ROOMKIND().surcharge_percentage() / 100 *
                                                item.ROOMKIND().price() *
                                                sub;
                                rentalFormObservable.setPricePerDay(pricePerDay);
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
        RentalForm_insertMutation rentalFormInsertMutation = RentalForm_insertMutation
                .builder()
                .guestId(rentalFormObservable.getGuestId())
                .roomId(rentalFormObservable.getRoomId())
                .billId(rentalFormObservable.getBillId())
                .amount(rentalFormObservable.getAmount())
                .rentalDays(rentalFormObservable.getRentalDays())
                .numOfGuests(rentalFormObservable.getNumOfGuests())
                .isResolved(rentalFormObservable.getIsResolved())
                .pricePerDay(rentalFormObservable.getPricePerDay())
                .startDay(rentalFormObservable.getStartDate())
                .build();
        Hasura.apolloClient.mutate(rentalFormInsertMutation)
                .enqueue(new ApolloCall.Callback<RentalForm_insertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalForm_insertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<RentalFormObservable> temp = modelState.getValue();
                            temp.add(rentalFormObservable);
                            modelState.postValue(temp);
                            //System.out.println(response.getData().insert_RENTALFORM());
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
        Hasura.apolloClient.query(new RentalForm_AllQuery())
                .enqueue(new ApolloCall.Callback<RentalForm_AllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<RentalForm_AllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<RentalFormObservable> rentalFormObservables = modelState.getValue();
                            response.getData().RENTALFORM().forEach(item -> {
                                Date startDate = item.start_date() != null ? Date.valueOf(item.start_date().toString()) : null;
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;

                                RentalFormObservable temp = new RentalFormObservable(
                                        item.id(),
                                        item.guest_id(),
                                        item.room_id(),
                                        item.bill_id(),
                                        item.amount(),
                                        item.rental_days(),
                                        item.number_of_guests(),
                                        item.is_resolved(),
                                        item.price_per_day(),
                                        startDate,
                                        createdAt,
                                        updatedAt
                                );
                                //System.out.println(item.start_date());
                                rentalFormObservables.add(temp);
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
        dataLoaded = true;
    }

}
