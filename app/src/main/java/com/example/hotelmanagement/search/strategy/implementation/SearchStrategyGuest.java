package com.example.hotelmanagement.search.strategy.implementation;

import android.app.SearchManager;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.observables.GuestKindObservable;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.search.strategy.abstraction.SearchStrategy;
import com.example.hotelmanagement.viewmodel.implementation.GuestKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.GuestViewModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchStrategyGuest extends SearchStrategy<GuestObservable> {

    @NonNull
    protected GuestViewModel guestViewModel;
    @NonNull
    protected GuestKindViewModel guestKindViewModel;

    public SearchStrategyGuest(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        this.guestViewModel = new ViewModelProvider
                (fragmentActivity).get(GuestViewModel.class);
        this.guestKindViewModel = new ViewModelProvider
                (fragmentActivity).get(GuestKindViewModel.class);

        suggestionVs.addAll(Arrays.asList(
                "n <name>",
                "a <address>",
                "id <id number>",
                "ca <dd-mm-yyyy>",
                "p <phone number>"
        ));
        suggestionKs.addAll(Arrays.asList(
                "Name?",
                "Address?",
                "ID Number?",
                "Created At?",
                "Phone Number?",
                "Use \",\" to separate search phrases"
        ));
    }

    @NonNull
    @Override
    public List<GuestObservable> processSearch(@NonNull String searchText) {

        List<GuestObservable> guestObservables = guestViewModel.getModelState(
        ).getValue();
        List<GuestKindObservable> guestKindObservables = guestKindViewModel.getModelState(
        ).getValue();

        if (guestObservables != null && guestKindObservables != null) {
            String[] searchPhrases = searchText.toUpperCase().split("\\s*,\\s*");
            for (String searchPhrase : searchPhrases) {
                if (searchPhrase.startsWith("N ")) {

                    Matcher matcher = Pattern.compile("N\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueName =
                                    matchedSearchText.replaceAll(regex, replacement);
                            guestObservables = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueName)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("A ")) {

                    Matcher matcher = Pattern.compile("A\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueAddress =
                                    matchedSearchText.replaceAll(regex, replacement);
                            guestObservables = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getAddress().toUpperCase().replaceAll(regex, replacement).contains(searchValueAddress)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("ID ")) {

                    Matcher matcher = Pattern.compile("ID\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueIdNumber =
                                    matchedSearchText.replaceAll(regex, replacement);
                            guestObservables = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getIdNumber().toUpperCase().replaceAll(regex, replacement).contains(searchValueIdNumber)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("CA ")) {

                    Matcher matcher = Pattern.compile("CA\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueCreatedAt =
                                    matchedSearchText.replaceAll(regex, replacement);
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            guestObservables = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getCreatedAt().format(dateTimeFormatter).toUpperCase().replaceAll(regex, replacement).contains(searchValueCreatedAt)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("P ")) {

                    Matcher matcher = Pattern.compile("P\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValuePhoneNumber =
                                    matchedSearchText.replaceAll(regex, replacement);
                            guestObservables = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getPhoneNumber().toUpperCase().replaceAll(regex, replacement).contains(searchValuePhoneNumber)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.matches("\\s*")) {
                    continue;
                }
                guestObservables = guestObservables.stream().filter(guestObservable -> false).collect(Collectors.toList());
                break;
            }
            return guestObservables;
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
