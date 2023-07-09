package com.example.hotelmanagement.viewmodels;

import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.example.common.Common;
import com.example.hasura.GuestByIdNumberQuery;
import com.example.hasura.GuestByIdQuery;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.observables.ExtendedObservable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ExtendedViewModel<BO extends BaseObservable> extends ViewModel {

    protected final MutableLiveData<List<BO>> modelState;
    protected final List<Runnable> subscriptionObservers;
    public Runnable onSuccessCallback;
    public Runnable onFailureCallback;
    public Function<List<Error>, Function<ApolloException, Consumer<ErrorInfo>>> on3ErrorsCallback;

    public ExtendedViewModel() {
        super();
        subscriptionObservers = new ArrayList<>();
        modelState = new MutableLiveData<>(new ArrayList<>());
    }

    public abstract void insert(@NonNull BO baseObservable);

    public abstract void update(@NonNull BO usedBaseObservable, @NonNull BO copyBaseObservable);

    public abstract void delete(@NonNull BO baseObservable);

    public abstract void startSubscription();

    public LiveData<List<BO>> getModelState() {
        return modelState;
    }

    public void fillSubscriptionObservers(List<Runnable> newSubscriptionObservers) {
        subscriptionObservers.addAll(newSubscriptionObservers);
    }

    public void clearSubscriptionObservers() {
        subscriptionObservers.clear();
    }

    public void insertSubscriptionObserver(Runnable subscriptionObserver) {
        subscriptionObservers.add(subscriptionObserver);
    }

    public void removeSubscriptionObserver(Runnable subscriptionObserver) {
        subscriptionObservers.remove(subscriptionObserver);
    }

    public void notifySubscriptionObservers() {
        subscriptionObservers.removeIf(Objects::isNull);
        subscriptionObservers.forEach(Runnable::run);
    }

    public void repostModelState() {
        modelState.postValue(modelState.getValue());
    }

    public void resetModelState() {
        modelState.setValue(modelState.getValue());
    }

    public void onSuccessHandler() {
        if (onSuccessCallback != null) {
            onSuccessCallback.run();
            onSuccessCallback = null;
        }
    }

    public void onFailureHandler() {
        if (onFailureCallback != null) {
            onFailureCallback.run();
            onFailureCallback = null;
        }
    }

    public void on3ErrorsHandler(@Nullable List<Error> apolloErrors, @Nullable ApolloException apolloException, @Nullable ErrorInfo cloudinaryErrorInfo) {
        if (on3ErrorsCallback != null) {
            on3ErrorsCallback.apply(apolloErrors).apply(apolloException).accept(cloudinaryErrorInfo);
            on3ErrorsCallback = null;
        }
    }

    public void cloudinaryRemoveImage(@NonNull String imageURL) {
        String regex = "ParadisePalms_HMS/[^/]+(?=\\.[^/]+$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(imageURL);
        if (matcher.find()) {
            try {
                MediaManager.get().getCloudinary().uploader().destroy(matcher.group(), Collections.singletonMap("invalidate", true));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void findGuestByGuestId(@Nullable Integer guestId, @NonNull Consumer<GuestByIdQuery.GUEST_by_pk> onSuccessCallback, @NonNull Runnable onFailureCallback) {
        if (guestId == null) {
            onFailureCallback.run();
            Log.e("ExtendedViewModel Find Guest By GuestId Error", "GuestId Is NULL");
        } else {
            GuestByIdQuery guestByIdQuery = GuestByIdQuery.builder().id(guestId).build();
            Hasura.requireInstance().requireApolloClient().query(guestByIdQuery).enqueue(new ApolloCall.Callback<GuestByIdQuery.Data>() {
                @Override
                public void onResponse(@NonNull Response<GuestByIdQuery.Data> response) {
                    if (response.getData() != null) {
                        GuestByIdQuery.GUEST_by_pk guest_by_pk = response.getData().GUEST_by_pk();
                        if (guest_by_pk != null) {
                            onSuccessCallback.accept(guest_by_pk);
                            Log.d("ExtendedViewModel Find Guest By GuestId Response Debug", guest_by_pk.toString());
                        } else {
                            onFailureCallback.run();
                            Log.d("ExtendedViewModel Find Guest By GuestId Response Debug", "Guest Not Found");
                        }
                    }
                    if (response.getErrors() != null) {
                        onFailureCallback.run();
                        response.getErrors().forEach(error -> Log.e("ExtendedViewModel Find Guest By GuestId Error", error.toString()));
                    }
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    e.printStackTrace();
                    onFailureCallback.run();
                }
            });
        }
    }

    public void findGuestByGuestIdNumber(@Nullable String guestIdNumber, @NonNull Consumer<GuestByIdNumberQuery.GUEST> onSuccessCallback, @NonNull Runnable onFailureCallback) {
        if (guestIdNumber == null) {
            onFailureCallback.run();
            Log.e("ExtendedViewModel Find Guest By GuestIdNumber Error", "GuestIdNumber Is NULL");
        } else {
            GuestByIdNumberQuery guestByIdNumberQuery = GuestByIdNumberQuery.builder().idNumber(guestIdNumber).build();
            Hasura.requireInstance().requireApolloClient().query(guestByIdNumberQuery).enqueue(new ApolloCall.Callback<GuestByIdNumberQuery.Data>() {
                @Override
                public void onResponse(@NonNull Response<GuestByIdNumberQuery.Data> response) {
                    if (response.getData() != null) {
                        List<GuestByIdNumberQuery.GUEST> guests = response.getData().GUEST();
                        if (guests.isEmpty()) {
                            onFailureCallback.run();
                            Log.d("ExtendedViewModel Find Guest By GuestIdNumber Response Debug", "Guest Not Found");
                        } else {
                            onSuccessCallback.accept(guests.get(0));
                            Log.d("ExtendedViewModel Find Guest By GuestIdNumber Response Debug", guests.get(0).toString());
                        }
                    }
                    if (response.getErrors() != null) {
                        onFailureCallback.run();
                        response.getErrors().forEach(error -> Log.e("ExtendedViewModel Find Guest By GuestIdNumber Error", error.toString()));
                    }
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    e.printStackTrace();
                    onFailureCallback.run();
                }
            });
        }
    }

    @Nullable
    public BO getObservable(@Nullable Integer baseObservableId) {
        BO baseObservable = null;
        if (baseObservableId != null) {
            List<BO> baseObservables = modelState.getValue();
            if (baseObservables != null) {
                Optional<BO> optionalBaseObservable = baseObservables.stream().filter
                        (_baseObservable_ -> _baseObservable_ instanceof ExtendedObservable
                                && ((ExtendedObservable) _baseObservable_).getId().equals(baseObservableId)).findFirst();
                if (optionalBaseObservable.isPresent()) {
                    baseObservable = optionalBaseObservable.get();
                }
            }
        }
        return baseObservable;
    }

    @NonNull
    public Boolean checkObservable(@NonNull BO baseObservable, @NonNull Context context, @NonNull View snackBarAttachView, @NonNull String... excludedFields)
            throws IllegalAccessException {
        for (Field field : baseObservable.getClass().getDeclaredFields()) {
            if (Arrays.stream(excludedFields).noneMatch(excludedField -> excludedField.equals(field.getName()))) {
                field.setAccessible(true);
                Object value = field.get(baseObservable);
                if (value == null || value.toString().equals("")) {
                    String begin = "";
                    String regex = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";
                    String message = ("missing " + Arrays.stream(field.getName().split(regex)).reduce(begin, (sentence, word) -> sentence + " " + word)).toUpperCase();
                    Common.showCustomSnackBar(message, context, snackBarAttachView);
                    return false;
                }
            }
        }
        return true;
    }

}
