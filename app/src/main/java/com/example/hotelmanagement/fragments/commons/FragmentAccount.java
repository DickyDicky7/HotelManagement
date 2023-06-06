package com.example.hotelmanagement.fragments.commons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.auth0.android.result.UserProfile;
import com.bumptech.glide.Glide;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAccountBinding;

import java.util.Map;
import java.util.function.Consumer;

public class FragmentAccount extends Fragment {

    private FragmentAccountBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Hasura.credentials != null) {

            UserProfile userProfile = Hasura.credentials.getUser();
            if (userProfile.getPictureURL() != null)
                Glide.with(this).load(userProfile.getPictureURL()).circleCrop().into(binding.imageAvatar);
            binding.emailValueTextView.setText(userProfile.getEmail() != null ? userProfile.getEmail() : "");
            binding.idValueTextView.setText(userProfile.getId() != null ? userProfile.getId().substring(6) : "");

            Map<String, Object> userMetaData = userProfile.getUserMetadata();
            binding.roleTextView.setText(userMetaData.getOrDefault("roles", "").toString());
            binding.nameTextView.setText(userMetaData.getOrDefault("real_name", "").toString());
            binding.genderValueTextView.setText(userMetaData.getOrDefault("gender", "").toString());
            binding.birthDayValueTextView.setText(userMetaData.getOrDefault("date_of_birth", "").toString());
            binding.phoneNumberValueTextView.setText(userMetaData.getOrDefault("phone_number", "").toString());

        }

        binding.signOutButton.setOnClickListener(_view_ -> {
            Consumer<Void> onSuccessCallback = unused -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentAccount_to_fragmentLogin);
            Consumer<Void> onFailureCallback = null;
            Hasura.logout
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
