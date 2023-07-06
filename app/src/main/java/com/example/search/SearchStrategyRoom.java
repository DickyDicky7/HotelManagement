package com.example.search;

import android.app.SearchManager;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchStrategyRoom extends SearchStrategy<RoomObservable> {

    @NonNull
    protected RoomViewModel roomViewModel;
    @NonNull
    protected RoomKindViewModel roomKindViewModel;

    public SearchStrategyRoom(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        this.roomViewModel = new ViewModelProvider
                (fragmentActivity).get(RoomViewModel.class);
        this.roomKindViewModel = new ViewModelProvider
                (fragmentActivity).get(RoomKindViewModel.class);

        suggestionVs.addAll(Arrays.asList(
                "k <kind>",
                "n <name>",
                "q <quantity>",
                "o <yes or no>"
        ));
        suggestionKs.addAll(Arrays.asList(
                "Kind?",
                "Name?",
                "Quantity?",
                "Occupied?",
                "Use \",\" to separate search phrases"
        ));
    }

    @NonNull
    @Override
    public List<RoomObservable> processSearch(@NonNull String searchText) {

        List<RoomObservable> roomObservables = roomViewModel.getModelState(
        ).getValue();
        List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState(
        ).getValue();

        if (roomObservables != null && roomKindObservables != null) {
            String[] searchPhrases = searchText.toUpperCase().split("\\s*,\\s*");
            for (String searchPhrase : searchPhrases) {
                if (searchPhrase.startsWith("K ")) {

                    Matcher matcher = Pattern.compile("K\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueKind =
                                    matchedSearchText.replaceAll(regex, replacement);

                            List<RoomKindObservable> _roomKindObservables_ = roomKindObservables
                                    .stream()
                                    .filter(roomKindObservable -> roomKindObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueKind)).collect(Collectors.toList());

                            roomObservables = roomObservables
                                    .stream()
                                    .filter(roomObservable -> _roomKindObservables_
                                            .stream()
                                            .anyMatch(roomKindObservable -> roomKindObservable.getId().equals(roomObservable.getRoomKindId())))
                                    .collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("N ")) {

                    Matcher matcher = Pattern.compile("N\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueName =
                                    matchedSearchText.replaceAll(regex, replacement);
                            roomObservables = roomObservables
                                    .stream()
                                    .filter(roomObservable -> roomObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueName)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("Q ")) {

                    Matcher matcher = Pattern.compile("Q\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueQuantity = Integer.parseInt(matchedSearchText);

                                List<RoomKindObservable> _roomKindObservables_ = roomKindObservables
                                        .stream()
                                        .filter(roomKindObservable -> roomKindObservable
                                                .getCapacity().equals(searchValueQuantity)).collect(Collectors.toList());

                                roomObservables = roomObservables
                                        .stream()
                                        .filter(roomObservable -> _roomKindObservables_
                                                .stream()
                                                .anyMatch(roomKindObservable -> roomKindObservable.getId().equals(roomObservable.getRoomKindId())))
                                        .collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                } else if (searchPhrase.startsWith("O ")) {

                    if ("O YES".equals(searchPhrase.trim())) {
                        roomObservables = roomObservables
                                .stream()
                                .filter(RoomObservable::getIsOccupied).collect(Collectors.toList());
                        continue;
                    } else if ("O NO".equals(searchPhrase.trim())) {
                        roomObservables = roomObservables
                                .stream()
                                .filter(roomObservable -> !roomObservable.getIsOccupied()).collect(Collectors.toList());
                        continue;
                    }

                } else if (searchPhrase.matches("\\s*")) {
                    continue;
                }
                roomObservables = roomObservables.stream().filter(roomObservable -> false).collect(Collectors.toList());
                break;
            }
            return roomObservables;
        }
        return new ArrayList<>();
    }

    @NonNull
    @Override
    public CursorAdapter getSuggestionsAdapter(@NonNull String searchText) {

        MatrixCursor matrixCursor = new MatrixCursor(new String[]{BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1});
        for (int i = 0; i < suggestionKs.size(); ++i) {
            matrixCursor.addRow(new Object[]{i, suggestionKs.get(i)});
        }
        cursorAdapter.changeCursor(matrixCursor);
        return cursorAdapter;

    }

}
