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
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.example.hotelmanagement.viewmodels.GuestViewModel;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SearchStrategyRentalForm extends SearchStrategy<RentalFormObservable> {

    @NonNull
    protected BillViewModel billViewModel;
    @NonNull
    protected RoomViewModel roomViewModel;
    @NonNull
    protected GuestViewModel guestViewModel;
    @NonNull
    protected RoomKindViewModel roomKindViewModel;
    @NonNull
    protected RentalFormViewModel rentalFormViewModel;

    public SearchStrategyRentalForm(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        billViewModel = new ViewModelProvider
                (fragmentActivity).get(BillViewModel.class);
        roomViewModel = new ViewModelProvider
                (fragmentActivity).get(RoomViewModel.class);
        guestViewModel = new ViewModelProvider
                (fragmentActivity).get(GuestViewModel.class);
        roomKindViewModel = new ViewModelProvider
                (fragmentActivity).get(RoomKindViewModel.class);
        rentalFormViewModel = new ViewModelProvider
                (fragmentActivity).get(RentalFormViewModel.class);

        suggestionVs.addAll(Arrays.asList(
                "r <yes or no>",
                "id <id number>",
                "ca <dd-mm-yyyy>",
                "sd <dd-mm-yyyy>",
                "ed <dd-mm-yyyy>",
                "dr <dd-mm-yyyy - dd-mm-yyyy>",
                "rd <rental days>",
                "rk <room kind>",
                "rn <room name>",
                "gn <guest name>",
                "gid <guest id number>",
                "bid <bill id number>"
        ));
        suggestionKs.addAll(Arrays.asList(
                "Resolved?",
                "ID Number?",
                "Created At?",
                "Start Date?",
                "End Date?",
                "Date Range?",
                "Rental Days?",
                "Room Kind?",
                "Room Name?",
                "Guest Name?",
                "Guest ID Number?",
                "Bill ID Number?",
                "Use \",\" to separate search phrases"
        ));

    }

    @NonNull
    @Override
    public List<RentalFormObservable> processSearch(@NonNull String searchText) {

        List<BillObservable> billObservables = billViewModel.getModelState(
        ).getValue();
        List<RoomObservable> roomObservables = roomViewModel.getModelState(
        ).getValue();
        List<GuestObservable> guestObservables = guestViewModel.getModelState(
        ).getValue();
        List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState(
        ).getValue();
        List<RentalFormObservable> rentalFormObservables = rentalFormViewModel.getModelState(
        ).getValue();

        if (billObservables != null && roomObservables != null && guestObservables != null && roomKindObservables != null && rentalFormObservables != null) {
            String[] searchPhrases = searchText.toUpperCase().split("\\s*,\\s*");
            for (String searchPhrase : searchPhrases) {
                if (searchPhrase.startsWith("R ")) {

                    if (searchPhrase.trim().startsWith("R YES")) {
                        rentalFormObservables = rentalFormObservables
                                .stream()
                                .filter(RentalFormObservable::getIsResolved).collect(Collectors.toList());
                        continue;
                    } else if (searchPhrase.trim().startsWith("R NO")) {
                        rentalFormObservables = rentalFormObservables
                                .stream()
                                .filter(rentalFormObservable -> !rentalFormObservable.getIsResolved()).collect(Collectors.toList());
                        continue;
                    }

                } else if (searchPhrase.startsWith("ID ")) {

                    Matcher matcher = Pattern.compile("ID\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueIdNumber = Integer.parseInt(matchedSearchText);
                                rentalFormObservables = rentalFormObservables
                                        .stream()
                                        .filter(rentalFormObservable -> rentalFormObservable
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
                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> rentalFormObservable
                                            .getCreatedAt().format(dateTimeFormatter).toUpperCase().replaceAll(regex, replacement).contains(searchValueCreatedAt)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("SD ")) {

                    Matcher matcher = Pattern.compile("SD\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueStartDate =
                                    matchedSearchText.replaceAll(regex, replacement);
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> rentalFormObservable
                                            .getStartDate().format(dateTimeFormatter).toUpperCase().replaceAll(regex, replacement).contains(searchValueStartDate)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("ED ")) {

                    Matcher matcher = Pattern.compile("ED\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueEndDate =
                                    matchedSearchText.replaceAll(regex, replacement);
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> rentalFormObservable.
                                            getCreatedAt().plusDays(rentalFormObservable.getRentalDays()).format(dateTimeFormatter).toUpperCase().replaceAll(regex, replacement).contains(searchValueEndDate)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("DR ")) {

                    Matcher matcher = Pattern.compile("DR\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueEndDate =
                                    matchedSearchText.replaceAll(regex, replacement);
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> rentalFormObservable.
                                            getCreatedAt().plusDays(rentalFormObservable.getRentalDays()).format(dateTimeFormatter).toUpperCase().replaceAll(regex, replacement).contains(searchValueEndDate)).collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("RD ")) {

                    Matcher matcher = Pattern.compile("RD\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueRentalDays = Integer.parseInt(matchedSearchText);
                                rentalFormObservables = rentalFormObservables
                                        .stream()
                                        .filter(rentalFormObservable -> rentalFormObservable
                                                .getRentalDays().equals(searchValueRentalDays)).collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                } else if (searchPhrase.startsWith("RK ")) {

                    Matcher matcher = Pattern.compile("RK\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueRoomKind =
                                    matchedSearchText.replaceAll(regex, replacement);

                            List<RoomKindObservable> _roomKindObservables_ = roomKindObservables
                                    .stream()
                                    .filter(roomKindObservable -> roomKindObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueRoomKind)).collect(Collectors.toList());

                            List<RoomObservable> _roomObservables_ = roomObservables
                                    .stream()
                                    .filter(roomObservable -> _roomKindObservables_
                                            .stream()
                                            .anyMatch(roomKindObservable -> roomKindObservable.getId().equals(roomObservable.getRoomKindId())))
                                    .collect(Collectors.toList());

                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> _roomObservables_
                                            .stream()
                                            .anyMatch(roomObservable -> roomObservable.getId().equals(rentalFormObservable.getRoomId())))
                                    .collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("RN ")) {

                    Matcher matcher = Pattern.compile("RN\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueRoomName =
                                    matchedSearchText.replaceAll(regex, replacement);

                            List<RoomObservable> _roomObservables_ = roomObservables
                                    .stream()
                                    .filter(roomObservable -> roomObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueRoomName)).collect(Collectors.toList());

                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> _roomObservables_
                                            .stream()
                                            .anyMatch(roomObservable -> roomObservable.getId().equals(rentalFormObservable.getRoomId())))
                                    .collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("GN ")) {

                    Matcher matcher = Pattern.compile("GN\\s(.+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            String regex = "\\s";
                            String replacement = "";
                            String searchValueName =
                                    matchedSearchText.replaceAll(regex, replacement);

                            List<GuestObservable> _guestObservables_ = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getName().toUpperCase().replaceAll(regex, replacement).contains(searchValueName)).collect(Collectors.toList());

                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> _guestObservables_
                                            .stream()
                                            .anyMatch(guestObservable -> guestObservable.getId().equals(rentalFormObservable.getGuestId())))
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

                            List<GuestObservable> _guestObservables_ = guestObservables
                                    .stream()
                                    .filter(guestObservable -> guestObservable
                                            .getIdNumber().toUpperCase().replaceAll(regex, replacement).contains(searchValueGuestIdNumber)).collect(Collectors.toList());

                            rentalFormObservables = rentalFormObservables
                                    .stream()
                                    .filter(rentalFormObservable -> _guestObservables_
                                            .stream()
                                            .anyMatch(guestObservable -> guestObservable.getId().equals(rentalFormObservable.getGuestId())))
                                    .collect(Collectors.toList());
                            continue;
                        }
                    }

                } else if (searchPhrase.startsWith("BID ")) {

                    Matcher matcher = Pattern.compile("BID\\s(\\d+)").matcher(searchPhrase);
                    if (matcher.find()) {
                        String matchedSearchText = matcher.group(1);
                        if (null != matchedSearchText) {
                            try {
                                int searchValueBillIdNumber = Integer.parseInt(matchedSearchText);
                                rentalFormObservables = rentalFormObservables
                                        .stream()
                                        .filter(rentalFormObservable -> rentalFormObservable
                                                .getBillId().equals(searchValueBillIdNumber)).collect(Collectors.toList());
                                continue;
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                } else if (searchPhrase.matches("\\s*")) {
                    continue;
                }
                rentalFormObservables = rentalFormObservables.stream().filter(rentalFormObservable -> false).collect(Collectors.toList());
                break;
            }
            return rentalFormObservables;
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
