package com.example.search;

import android.app.SearchManager;
import android.database.MatrixCursor;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.observables.BillObservable;
import com.example.hotelmanagement.observables.GuestObservable;
import com.example.hotelmanagement.observables.RentalFormObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.example.hotelmanagement.viewmodels.GuestViewModel;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchStrategyBill extends SearchStrategy<BillObservable> {

    @NonNull
    protected BillViewModel billViewModel;
    @NonNull
    protected GuestViewModel guestViewModel;
    @NonNull
    protected RentalFormViewModel rentalFormViewModel;

    public SearchStrategyBill(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        billViewModel = new ViewModelProvider
                (fragmentActivity).get(BillViewModel.class);
        guestViewModel = new ViewModelProvider
                (fragmentActivity).get(GuestViewModel.class);
        rentalFormViewModel = new ViewModelProvider
                (fragmentActivity).get(RentalFormViewModel.class);

        suggestionVs.addAll(Arrays.asList(
                "p <yes or no>",
                "id <id number>",
                "ca <dd-mm-yyyy>",
                "cr <lower bound - upper bound>",
                "gn <guest name>",
                "gid <guest id number>",
                "rid <rental form id number>"
        ));
        suggestionKs.addAll(Arrays.asList(
                "Paid?",
                "ID Number?",
                "Created At?",
                "Cost Range?",
                "Guest Name?",
                "Guest ID Number?",
                "Rental Form ID Number?",
                "Use \",\" to separate search phrases"
        ));

    }

    @NonNull
    @Override
    public List<BillObservable> processSearch(@NonNull String searchText) {

        List<BillObservable> billObservables = billViewModel.getModelState(
        ).getValue();
        List<GuestObservable> guestObservables = guestViewModel.getModelState(
        ).getValue();
        List<RentalFormObservable> rentalFormObservables = rentalFormViewModel.getModelState(
        ).getValue();

        if (billObservables != null && guestObservables != null && rentalFormObservables != null) {
            String[] searchPhrases = searchText.toUpperCase().split("\\s*,\\s*");
            for (String searchPhrase : searchPhrases) {
                if (searchPhrase.startsWith("P ")) {

                    if (searchPhrase.trim().startsWith("P YES")) {
                        billObservables = billObservables
                                .stream()
                                .filter(BillObservable::getIsPaid).collect(Collectors.toList());
                        continue;
                    } else if (searchPhrase.trim().startsWith("P NO")) {
                        billObservables = billObservables
                                .stream()
                                .filter(billObservable -> !billObservable.getIsPaid()).collect(Collectors.toList());
                        continue;
                    }

                } else if (searchPhrase.startsWith("ID ")) {

                    Matcher matcher = Pattern.compile("ID\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueIdNumber = Integer.parseInt(matchedSearchText);
                                billObservables = billObservables
                                        .stream()
                                        .filter(billObservable -> billObservable
                                                .getId().equals(searchValueIdNumber)).collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
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
                            billObservables = billObservables
                                    .stream()
                                    .filter(billObservable -> billObservable
                                            .getCreatedAt().format(dateTimeFormatter).toUpperCase().replaceAll(regex, replacement).contains(searchValueCreatedAt)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("CR ")) {

                    Matcher matcher = Pattern.compile("CR\\s(\\d+([.]\\d+)?)\\s*-\\s*(\\d+([.]\\d+)?)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText1 = matcher.group(1);
                        String matchedSearchText2 = matcher.group(3);
                        if (null != matchedSearchText1 && null != matchedSearchText2) {
                            try {
                                double searchValueLowerBoundCost = Double.parseDouble(matchedSearchText1);
                                double searchValueUpperBoundCost = Double.parseDouble(matchedSearchText2);
                                billObservables = billObservables
                                        .stream()
                                        .filter(billObservable -> {
                                            Double cost = billObservable.getCost();
                                            return cost <= searchValueUpperBoundCost && cost >= searchValueLowerBoundCost;
                                        }).collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                } else if (searchPhrase.startsWith("GN ")) {

                    Matcher matcher = Pattern.compile("GN\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueGuestName =
                                    matchedSearchText.replaceAll(regex, replacement);

                            List<GuestObservable> _guestObservables_ = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueGuestName)).collect(Collectors.toList());

                            billObservables = billObservables
                                    .stream()
                                    .filter(billObservable -> _guestObservables_
                                            .stream()
                                            .anyMatch(guestObservable -> guestObservable.getId().equals(billObservable.getGuestId())))
                                    .collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("GID ")) {

                    Matcher matcher = Pattern.compile("GID\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueGuestIdNumber =
                                    matchedSearchText.replaceAll(regex, replacement);

                            Optional<GuestObservable> optionalGuestObservable = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getIdNumber().toUpperCase().replaceAll(regex, replacement).contains(searchValueGuestIdNumber)).findFirst();

                            if (optionalGuestObservable.isPresent()) {
                                GuestObservable guestObservable = optionalGuestObservable.get();
                                billObservables = billObservables
                                        .stream()
                                        .filter(billObservable -> billObservable
                                                .getGuestId().equals(guestObservable.getId())).collect(Collectors.toList());
                                continue;
                            }
                        }
                    }

                } else if (searchPhrase.startsWith("RID ")) {

                    Matcher matcher = Pattern.compile("RID\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueRentalFormIdNumber = Integer.parseInt(matchedSearchText);

                                Optional<RentalFormObservable> optionalRentalFormObservable = rentalFormObservables
                                        .stream()
                                        .filter(rentalFormObservable -> rentalFormObservable
                                                .getId().equals(searchValueRentalFormIdNumber)).findFirst();

                                if (optionalRentalFormObservable.isPresent()) {
                                    RentalFormObservable rentalFormObservable = optionalRentalFormObservable.get();
                                    billObservables = billObservables
                                            .stream()
                                            .filter(billObservable -> billObservable
                                                    .getId().equals(rentalFormObservable.getBillId())).collect(Collectors.toList());
                                    continue;
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                } else if (searchPhrase.matches("\\s*")) {
                    continue;
                }
                billObservables = billObservables.stream().filter(billObservable -> false).collect(Collectors.toList());
                break;
            }
            return billObservables;
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
