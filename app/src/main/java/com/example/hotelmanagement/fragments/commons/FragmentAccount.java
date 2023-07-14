package com.example.hotelmanagement.fragments.commons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAccountBinding;
import com.example.hotelmanagement.dialog.implementation.DialogFragmentFailure;
import com.example.hotelmanagement.dialog.implementation.DialogFragmentSuccess;
import com.example.hotelmanagement.hasura.Hasura;

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

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Consumer<Credentials> credentialsConsumer = credentials -> {
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {

                    UserProfile userProfile = credentials.getUser();

                    if (userProfile.getId() != null) {
                        int beginIndex = 6;
                        binding.idValueTextView.setText(userProfile.getId().substring(beginIndex));
                    } else {
                        binding.idValueTextView.setText("");
                    }

                    if (userProfile.getEmail() != null) {
                        binding.emailValueTextView.setText(userProfile.getEmail());
                    } else {
                        binding.emailValueTextView.setText("");
                    }

                    YoYo.with(Techniques.FadeInDown).duration(500).playOn(binding.avatarImageView);
                    if (userProfile.getPictureURL() != null) {
                        Glide.with(this).load(userProfile.getPictureURL()).circleCrop().into(binding.avatarImageView);
                    }

                    Map<String, Object> userMetaData = userProfile.getUserMetadata();

                    String keyRole = "roles";
                    Object objRole = userMetaData.get(keyRole);
                    if (null == objRole) {
                        binding.roleTextView.setText("");
                    } else {
                        binding.roleTextView.setText(objRole.toString());
                    }

                    String keyName = "real_name";
                    Object objName = userMetaData.get(keyName);
                    if (null == objName) {
                        binding.nameTextView.setText("");
                    } else {
                        binding.nameTextView.setText(objName.toString());
                    }

                    String keyGender = "gender";
                    Object objGender = userMetaData.get(keyGender);
                    if (null == objGender) {
                        binding.genderValueTextView.setText("");
                    } else {
                        binding.genderValueTextView.setText(objGender.toString());
                    }

                    String keyBirthDay = "date_of_birth";
                    Object objBirthDay = userMetaData.get(keyBirthDay);
                    if (null == objBirthDay) {
                        binding.birthDayValueTextView.setText("");
                    } else {
                        binding.birthDayValueTextView.setText(objBirthDay.toString());
                    }

                    String keyPhoneNumber = "phone_number";
                    Object objPhoneNumber = userMetaData.get(keyPhoneNumber);
                    if (null == objPhoneNumber) {
                        binding.phoneNumberValueTextView.setText("");
                    } else {
                        binding.phoneNumberValueTextView.setText(objPhoneNumber.toString());
                    }

                });
            }
        };
        Consumer<CredentialsManagerException> credentialsManagerExceptionConsumer = null;
        Hasura.requireInstance().getCredentials(credentialsConsumer, credentialsManagerExceptionConsumer);

        binding.signOutButton.setOnClickListener(_view_ -> {
            Runnable onSuccessCallback = () -> {
                Hasura.destroyInstance();
                NavHostFragment.findNavController(this).navigate(R.id.action_fragmentAccount_to_fragmentLogin);
            };
            Runnable onFailureCallback = null;
            Consumer<AuthenticationException> authenticationExceptionConsumer = null;
            Hasura.requireInstance
                    ().Logout
                    (
                            requireContext(),
                            onSuccessCallback,
                            onFailureCallback,
                            authenticationExceptionConsumer
                    );
        });

        binding.changePasswordButton.setOnClickListener(_view_ -> {
            Runnable onSuccessCallback = () -> {
                String successTag = "FragmentAccount Success";
                String successMessage = "Success: An email has been successfully sent to your registered email address with instructions to reset your password.";
                DialogFragmentSuccess.newOne(getParentFragmentManager(), successTag, successMessage);
            };
            Runnable onFailureCallback = null;
            Consumer<AuthenticationException> authenticationExceptionConsumer = e -> {
                String failureTag = "FragmentAccount Failure";
                String failureMessage = "Failure: " + e.getMessage();
                DialogFragmentFailure.newOne(getParentFragmentManager(), failureTag, failureMessage);
            };
            Hasura.requireInstance().resetPassword(
                    onSuccessCallback,
                    onFailureCallback,
                    authenticationExceptionConsumer);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
