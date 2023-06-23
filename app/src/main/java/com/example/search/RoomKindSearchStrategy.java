package com.example.search;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class RoomKindSearchStrategy extends SearchStrategy<RoomKindViewModel> {

    public RoomKindSearchStrategy(RoomKindViewModel viewModel) {
        super(viewModel);
    }

    @Override
    public void processSearch(@NonNull String searchText) {
        List<RoomKindObservable> roomKindObservables = viewModel.getModelState().getValue();
        if (roomKindObservables != null) {
            String[] searchPhrases = searchText.toUpperCase().split("\\s*,\\s*");
            System.out.println(searchPhrases.length);
            //for (String s : searchPhrases) System.out.println(s);
            for (String searchPhrase : searchPhrases) {
                if (searchPhrase.startsWith("N ")) {
                    //System.out.println(searchPhrase.replaceAll(" ", ""));
                    roomKindObservables = roomKindObservables.stream()
                            .filter(roomKindObservable -> ("N" + roomKindObservable.getName().toUpperCase().replaceAll(" ", "")).contains(searchPhrase.replaceAll(" ", "")))
                            .collect(Collectors.toList());
                } else if (searchPhrase.startsWith("C ")) {
                    String[] searchWords = searchPhrase.split("\\s+");
                    for (String s : searchWords) System.out.println(s);
                    System.out.println(searchWords.length);
                    if (searchWords.length == 2) {
                        roomKindObservables = roomKindObservables.stream().filter(roomKindObservable -> roomKindObservable.getCapacity().toString().equals(searchWords[1])).collect(Collectors.toList());
                    }
                } else if (searchPhrase.startsWith("B ")) {
                    String[] searchWords = searchPhrase.split("\\s+");
                    if (searchWords.length == 2) {
                        roomKindObservables = roomKindObservables.stream().filter(roomKindObservable -> roomKindObservable.getNumberOfBeds().toString().equals(searchWords[1])).collect(Collectors.toList());
                    }
                } else if (searchPhrase.startsWith("A ")) {
                    String[] searchWords = searchPhrase.split("\\s+");
                    if (searchWords.length == 2) {
                        roomKindObservables = roomKindObservables.stream().filter(roomKindObservable -> {
                            try {
                                return roomKindObservable.getArea().equals(Double.valueOf(searchWords[1]));
                            } catch (NumberFormatException e) {
                                return false;
                            }
                        }).collect(Collectors.toList());
                    }
                }
            }
            roomKindObservables.forEach(observable -> Log.i("", observable.getName() + " " + observable.getId()));
        }
    }

}
