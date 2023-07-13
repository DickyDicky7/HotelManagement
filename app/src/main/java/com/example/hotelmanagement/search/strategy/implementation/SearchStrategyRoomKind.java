package com.example.hotelmanagement.search.strategy.implementation;

import android.app.SearchManager;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.search.strategy.abstraction.SearchStrategy;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchStrategyRoomKind extends SearchStrategy<RoomKindObservable> {

    @NonNull
    protected RoomKindViewModel roomKindViewModel;

    public SearchStrategyRoomKind(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.roomKindViewModel = new ViewModelProvider(fragmentActivity).get(RoomKindViewModel.class);
        suggestionVs.addAll(Arrays.asList(
                "a <area>",
                "n <name>",
                "c <capacity>",
                "b <number of beds>"
        ));
        suggestionKs.addAll(Arrays.asList(
                "Area?",
                "Name?",
                "Capacity?",
                "Number Of Beds?",
                "Use \",\" to separate search phrases"
        ));
    }

    @NonNull
    @Override
    public List<RoomKindObservable> processSearch(@NonNull String searchText) {
        List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState().getValue();
        if (roomKindObservables != null) {
            String[] searchPhrases = searchText.toUpperCase().split("\\s*,\\s*");
            for (String searchPhrase : searchPhrases) {
                if (searchPhrase.startsWith("A ")) {

                    Matcher matcher = Pattern.compile("A\\s(\\d+([.]\\d+)?)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                double searchValueArea = Double.parseDouble(matchedSearchText);
                                roomKindObservables = roomKindObservables
                                        .stream()
                                        .filter(roomKindObservable -> roomKindObservable
                                                .getArea().equals(searchValueArea)).collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
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
                            roomKindObservables = roomKindObservables
                                    .stream()
                                    .filter(roomKindObservable -> roomKindObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueName)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("C ")) {

                    Matcher matcher = Pattern.compile("C\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueCapacity = Integer.parseInt(matchedSearchText);
                                roomKindObservables = roomKindObservables
                                        .stream()
                                        .filter(roomKindObservable -> roomKindObservable
                                                .getCapacity().equals(searchValueCapacity)).collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                } else if (searchPhrase.startsWith("B ")) {

                    Matcher matcher = Pattern.compile("B\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueNumberOfBeds = Integer.parseInt(matchedSearchText);
                                roomKindObservables = roomKindObservables
                                        .stream()
                                        .filter(roomKindObservable -> roomKindObservable.
                                                getNumberOfBeds().equals(searchValueNumberOfBeds)).collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                } else if (searchPhrase.matches("\\s*")) {
                    continue;
                }
                roomKindObservables = roomKindObservables.stream().filter(roomKindObservable -> false).collect(Collectors.toList());
                break;
            }
            return roomKindObservables;
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
