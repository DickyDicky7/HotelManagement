package com.example.hotelmanagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.example.hotelmanagement.databinding.FragmentTempHomeBinding;

public class FragmentTempHome extends Fragment {

    private FragmentTempHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTempHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.button6.setOnClickListener(_view_ -> {
            Auth0 account = new Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain));
            WebAuthProvider.login(account).start(requireContext(), new Callback<Credentials, AuthenticationException>() {
                @Override
                public void onSuccess(Credentials credentials) {
                    System.out.println(credentials.getUser().getEmail());
                }

                @Override
                public void onFailure(@NonNull AuthenticationException e) {
                    System.out.println(e);
                }
            });
        });

        binding.button.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddRoom);
        });

        binding.button2.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddGuest);
        });

        binding.button3.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddRoomKind);
        });

        binding.button4.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddBill);
        });

        binding.button5.setOnClickListener(_view -> {
            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentTempHome_to_fragmentAddRentalForm);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}