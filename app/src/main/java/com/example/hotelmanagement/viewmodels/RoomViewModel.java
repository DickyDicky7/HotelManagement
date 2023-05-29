package com.example.hotelmanagement.viewmodels;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.hasura.Hasura;
import com.example.hasura.Room_AllQuery;
import com.example.hasura.Room_InsertMutation;
import com.example.hotelmanagement.ActivityMain;
import com.example.hotelmanagement.observables.RoomObservable;

import java.sql.Timestamp;
import java.util.List;

public class RoomViewModel extends ExtendedViewModel<RoomObservable> {

    public RoomViewModel() {
        super();
    }

    public void checkObservable(RoomObservable roomObservable) {
        if (roomObservable.getName().equals("")) {
            Toast.makeText(ActivityMain.getInstance(), "Name is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        if (roomObservable.getNote().equals("")) {
            roomObservable.setNote(null);
        }
        if (roomObservable.getDescription().equals("")) {
            roomObservable.setDescription(null);
        }
        if (roomObservable.getRoomKindId() == null) {
            Toast.makeText(ActivityMain.getInstance(), "Kind of room is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        insert(roomObservable);
    }

    public void insert(RoomObservable roomObservable) {
        Room_InsertMutation roomInsertMutation = Room_InsertMutation
                .builder()
                .name(roomObservable.getName())
                .note(roomObservable.getNote())
                .description(roomObservable.getDescription())
                .roomkind_id(roomObservable.getRoomKindId())
                .isOccupied(roomObservable.getIsOccupied())
                .build();
        Hasura.apolloClient.mutate(roomInsertMutation)
                .enqueue(new ApolloCall.Callback<Room_InsertMutation.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Room_InsertMutation.Data> response) {
                        if (response.getData() != null) {
                            List<RoomObservable> temp = modelState.getValue();
                            temp.add(roomObservable);
                            modelState.postValue(temp);
                            System.out.println(response.getData().insert_ROOM());
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
        Hasura.apolloClient.query(new Room_AllQuery())
                .enqueue(new ApolloCall.Callback<Room_AllQuery.Data>() {
                    @Override
                    public void onResponse(@NonNull Response<Room_AllQuery.Data> response) {
                        if (response.getData() != null) {
                            List<RoomObservable> roomObservables = modelState.getValue();
                            response.getData().ROOM().forEach(item -> {
                                Timestamp createdAt = item.created_at() != null ? Timestamp.valueOf(item.created_at().toString().replaceAll("T", " ")) : null;
                                Timestamp updatedAt = item.updated_at() != null ? Timestamp.valueOf(item.updated_at().toString().replaceAll("T", " ")) : null;
                                RoomObservable temp = new RoomObservable(
                                        item.id(),
                                        item.name(),
                                        item.note(),
                                        item.is_occupied(),
                                        item.roomkind_id(),
                                        item.description(),
                                        createdAt,
                                        updatedAt
                                );
                                roomObservables.add(temp);
                            });
                            modelState.postValue(roomObservables);
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
        dataLoaded = true;
    }

}
