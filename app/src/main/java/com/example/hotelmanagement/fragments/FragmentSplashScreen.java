package com.example.hotelmanagement.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentSplashScreenBinding;

@SuppressLint("CustomSplashScreen")
public class FragmentSplashScreen extends Fragment {

    private final int delayMilliseconds = 3000;
    private final Handler handler = new Handler();
    private final Runnable navigationCallback = () -> NavHostFragment.findNavController(this).navigate(R.id.action_fragmentSplashScreen_to_fragmentLogin);
    private FragmentSplashScreenBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.postDelayed(navigationCallback, delayMilliseconds);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(navigationCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(navigationCallback, delayMilliseconds);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacks(navigationCallback);
    }

}