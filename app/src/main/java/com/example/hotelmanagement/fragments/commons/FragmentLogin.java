package com.example.hotelmanagement.fragments.commons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hasura.Hasura;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentLoginBinding;

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
//        if (Hasura.logoutSuccessfully) System.out.println("LOG OUT SUCCESSFULLY");
        binding.btnSignIn.setOnClickListener(_view_ -> {
            Consumer<Void> onSuccessCallback = unused -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentLogin_to_fragmentTempHome);
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
