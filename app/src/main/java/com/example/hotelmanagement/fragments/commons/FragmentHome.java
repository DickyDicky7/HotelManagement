package com.example.hotelmanagement.fragments.commons;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.auth0.android.authentication.storage.CredentialsManagerException;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentHomeBinding;
import com.example.hotelmanagement.hasura.Hasura;

import java.util.Map;
import java.util.function.Consumer;

public class FragmentHome extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressWarnings("ConstantConditions")
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Consumer<Credentials> credentialsConsumer = credentials -> {
            if (getActivity() != null) {
                requireActivity().runOnUiThread(() -> {

                    UserProfile userProfile = credentials.getUser();
                    YoYo.with(Techniques.FadeInLeft).duration(500).playOn(binding.imageAvatar);
                    if (userProfile.getPictureURL() != null) {
                        Glide.with(this).load(userProfile.getPictureURL()).centerCrop().transform(new RoundedCorners(30)).into(binding.imageAvatar);
                    }

                    Map<String, Object> userMetaData = userProfile.getUserMetadata();
                    String keyName = "real_name";
                    Object objName = userMetaData.get(keyName);
                    if (null == objName) {
                        binding.txtUser.setText("");
                    } else {
                        binding.txtUser.setText(objName.toString());
                    }

                });
            }
        };
        Consumer<CredentialsManagerException> credentialsManagerExceptionConsumer = null;
        Hasura.requireInstance().getCredentials(credentialsConsumer, credentialsManagerExceptionConsumer);

        binding.roomButton.setOnClickListener(_view_
                -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentRooms));
        binding.billButton.setOnClickListener(_view_
                -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentBills));
        binding.guestButton.setOnClickListener(_view_
                -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentGuests));
        binding.reportButton.setOnClickListener(_view_
                -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentReport));
        binding.roomKindButton.setOnClickListener(_view_
                -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentRoomKinds));
        binding.rentalFormButton.setOnClickListener(_view_
                -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentRentalForms));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
