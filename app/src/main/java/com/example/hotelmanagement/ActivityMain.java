package com.example.hotelmanagement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hotelmanagement.databinding.ActivityMainBinding;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

}