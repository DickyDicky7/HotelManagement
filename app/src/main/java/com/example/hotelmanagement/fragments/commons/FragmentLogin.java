package com.example.hotelmanagement.fragments.commons;

import android.content.Context;
import android.os.Bundle;
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
import com.cloudinary.Configuration;
import com.cloudinary.android.MediaManager;
import com.example.hasura.CloudinaryAllQuery;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentLoginBinding;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.example.hotelmanagement.viewmodels.GuestKindViewModel;
import com.example.hotelmanagement.viewmodels.GuestViewModel;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

import java.util.function.Consumer;

public class FragmentLogin extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSignIn.setOnClickListener(_view_ -> {

            Consumer<Void> onSuccessCallback = unused -> {
                BillViewModel billViewModel = new ViewModelProvider(requireActivity()).get(BillViewModel.class);
                RoomViewModel roomViewModel = new ViewModelProvider(requireActivity()).get(RoomViewModel.class);
                GuestViewModel guestViewModel = new ViewModelProvider(requireActivity()).get(GuestViewModel.class);
                RoomKindViewModel roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
                GuestKindViewModel guestKindViewModel = new ViewModelProvider(requireActivity()).get(GuestKindViewModel.class);
                RentalFormViewModel rentalFormViewModel = new ViewModelProvider(requireActivity()).get(RentalFormViewModel.class);
                billViewModel.loadData();
                roomViewModel.loadData();
                guestViewModel.loadData();
                roomKindViewModel.loadData();
                guestKindViewModel.loadData();
                rentalFormViewModel.loadData();
                Context capturedRequiredContext = requireContext();
                Hasura.apolloClient.query(new CloudinaryAllQuery()).enqueue(new ApolloCall.Callback<CloudinaryAllQuery.Data>() {
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
                NavHostFragment.findNavController(this).navigate(R.id.action_fragmentLogin_to_fragmentHome);
            };
            Consumer<Void> onFailureCallback = null;
            Hasura.configAuth0
                    (
                            getString(R.string.com_auth0_client_id),
                            getString(R.string.com_auth0_domain),
                            getString(R.string.com_auth0_scheme)
                    );
            Hasura.login
                    (
                            requireContext(),
                            onSuccessCallback,
                            onFailureCallback
                    );

        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
