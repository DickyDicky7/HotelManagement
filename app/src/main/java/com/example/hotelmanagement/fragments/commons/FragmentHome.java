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

import com.auth0.android.result.UserProfile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.hasura.Hasura;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentHomeBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;

import java.util.Map;

public class FragmentHome extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (Hasura.credentials != null) {
            UserProfile userProfile = Hasura.credentials.getUser();
            if (userProfile.getPictureURL() != null)
                Glide.with(this).load(userProfile.getPictureURL()).centerCrop().transform(new RoundedCorners(30)).into(binding.imageAvatar);
            Map<String, Object> userMetaData = userProfile.getUserMetadata();
            binding.txtUser.setText(userMetaData.getOrDefault("real_name", "").toString());
        }

        binding.roomButton.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentRooms));
        binding.billButton.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentBills));
        binding.guestButton.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentGuests));
        binding.roomKindButton.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentRoomKinds));
        binding.rentalFormButton.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentRentalForms));

        binding.reportButton2.setOnClickListener(_view_ -> {
            FailureDialogFragment successDialogFragment = new FailureDialogFragment("");
            successDialogFragment.showNow(getParentFragmentManager(), "success");
//            NavHostFragment.findNavController(this).navigate(R.id.action_fragmentHome_to_fragmentAccount);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
