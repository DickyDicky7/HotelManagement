package com.example.hotelmanagement.fragments.commons;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.auth0.android.authentication.AuthenticationException;
import com.cloudinary.Configuration;
import com.cloudinary.android.MediaManager;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hasura.CloudinaryAllQuery;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentLoginBinding;
import com.example.hotelmanagement.hasura.Hasura;
import com.example.hotelmanagement.popupwindow.implementation.PopupWindowLoading;
import com.example.hotelmanagement.viewmodel.abstraction.ExtendedViewModel;
import com.example.hotelmanagement.viewmodel.implementation.BillViewModel;
import com.example.hotelmanagement.viewmodel.implementation.GuestKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.GuestViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RentalFormViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RoomViewModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class FragmentLogin extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    private PopupWindowLoading popupWindowLoading;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Hasura.getInstance() == null || Hasura.getInstance().currentLogState == Hasura.LogState.OUT) {
            binding.btnSignIn.setVisibility(View.INVISIBLE);
            int delayMilliseconds = 3000;
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if (binding != null && binding.btnSignIn.getVisibility() != View.VISIBLE) {
                    YoYo.with(Techniques.FadeIn).duration(300).onStart(animator -> {
                        if (binding != null) {
                            binding.btnSignIn.setVisibility(View.VISIBLE);
                        }
                    }).playOn(binding.btnSignIn);
                }
            }, delayMilliseconds);
        }

        binding.btnSignIn.setOnClickListener(_view_ -> {

            popupWindowLoading = PopupWindowLoading.newOne(getLayoutInflater(), binding.getRoot());
            popupWindowLoading.showAsDropDown(binding.getRoot());

            Runnable onSuccessCallback = () -> {
                if (getActivity() == null) {
                    return;
                }
                startSubscriptionExtendedViewModels();
                startMediaManager();
                NavHostFragment.findNavController(this).navigate(R.id.action_fragmentLogin_to_fragmentHome);
            };
            Runnable onFailureCallback = null;
            Consumer<AuthenticationException> authenticationExceptionConsumer = null;
            Hasura.requireInstance
                    (
                            getString(R.string.com_auth0_client_id),
                            getString(R.string.com_auth0_domain),
                            getString(R.string.com_auth0_scheme)
                    );
            Hasura.requireInstance
                    ().Login
                    (
                            requireContext(),
                            onSuccessCallback,
                            onFailureCallback,
                            authenticationExceptionConsumer
                    );

        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Hasura.getInstance() != null) {
            if (Hasura.getInstance().currentLogState == Hasura.LogState.IN) {
                startSubscriptionExtendedViewModels();
                startMediaManager();
                NavHostFragment.findNavController(this).navigate(R.id.action_fragmentLogin_to_fragmentHome);
            }
        }
    }

    protected void startMediaManager() {
        Context capturedRequiredContext = requireContext();
        if (Hasura.mediaManagerNotInitialized) {
            Hasura.requireInstance().requireApolloClient().query(new CloudinaryAllQuery()).enqueue(new ApolloCall.Callback<CloudinaryAllQuery.Data>() {
                @Override
                public void onResponse(@NonNull Response<CloudinaryAllQuery.Data> response) {
                    if (response.getData() != null) {
                        CloudinaryAllQuery.CLOUDINARY cloudinary = response.getData().CLOUDINARY().get(0);
                        Configuration configuration = new Configuration();
                        configuration.secure = true;
                        configuration.apiKey = cloudinary.api_key();
                        configuration.apiSecret = cloudinary.api_secret();
                        configuration.cloudName = cloudinary.cloud_name();
                        MediaManager.init(capturedRequiredContext, configuration);
                        Hasura.mediaManagerNotInitialized = false;
                    }
                    if (response.getErrors() != null) {
                        response.getErrors().forEach(error -> Log.e("FragmentLogin Initialize MediaManager Error", error.toString()));
                    }
                }

                @Override
                public void onFailure(@NonNull ApolloException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    protected void startSubscriptionExtendedViewModels() {
        Map<String, ExtendedViewModel<?>> _extendedViewModels = new HashMap<>();
        List<Class<? extends ExtendedViewModel<?>>> extendedViewModelClasses = Arrays.asList(
                BillViewModel.class,
                RoomViewModel.class, GuestViewModel.class, RoomKindViewModel.class, GuestKindViewModel.class, RentalFormViewModel.class);
        for (Class<? extends ExtendedViewModel<?>> extendedViewModelClass : extendedViewModelClasses) {
            _extendedViewModels.put(extendedViewModelClass.getSimpleName(), new ViewModelProvider(requireActivity()).get(extendedViewModelClass));
        }
        _extendedViewModels.forEach((extendedViewModelSimpleName, extendedViewModel) -> {
            @Nullable
            Runnable subscriptionObserver = null;
            switch (extendedViewModelSimpleName) {

                case "BillViewModel": {
                    ExtendedViewModel<?> rentalFormViewModel = _extendedViewModels.get("RentalFormViewModel");
                    if (rentalFormViewModel != null) {
                        subscriptionObserver = rentalFormViewModel::repostModelState;
                    }
                }
                break;

                case "RoomViewModel": {
                    ExtendedViewModel<?> rentalFormViewModel = _extendedViewModels.get("RentalFormViewModel");
                    ExtendedViewModel<?> billViewModel = _extendedViewModels.get("BillViewModel");
                    if (billViewModel != null && rentalFormViewModel != null) {
                        subscriptionObserver = () -> {
                            billViewModel.repostModelState();
                            rentalFormViewModel.repostModelState();
                        };
                    }
                }
                break;

                case "RoomKindViewModel": {
                    ExtendedViewModel<?> roomViewModel = _extendedViewModels.get("RoomViewModel");
                    if (roomViewModel != null) {
                        subscriptionObserver = roomViewModel::repostModelState;
                    }
                }
                break;

                case "GuestViewModel": {
                    ExtendedViewModel<?> rentalFormViewModel = _extendedViewModels.get("RentalFormViewModel");
                    ExtendedViewModel<?> billViewModel = _extendedViewModels.get("BillViewModel");
                    if (billViewModel != null && rentalFormViewModel != null) {
                        subscriptionObserver = () -> {
                            billViewModel.repostModelState();
                            rentalFormViewModel.repostModelState();
                        };
                    }
                }
                break;

                case "GuestKindViewModel": {
                    ExtendedViewModel<?> guestViewModel = _extendedViewModels.get("GuestViewModel");
                    if (guestViewModel != null) {
                        subscriptionObserver = guestViewModel::repostModelState;
                    }
                }
                break;

                case "RentalFormViewModel": {
                    ExtendedViewModel<?> billViewModel = _extendedViewModels.get("BillViewModel");
                    if (billViewModel != null) {
                        subscriptionObserver = billViewModel::repostModelState;
                    }
                }
                break;

                default: {
                }
                break;

            }
            if (subscriptionObserver != null) {
                extendedViewModel.startSubscription();
                extendedViewModel.insertSubscriptionObserver(subscriptionObserver);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
    }

}
