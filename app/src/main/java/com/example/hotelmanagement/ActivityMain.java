package com.example.hotelmanagement;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.databinding.ActivityMainBinding;
import com.example.hotelmanagement.viewmodels.BillViewModel;
import com.example.hotelmanagement.viewmodels.GuestKindViewModel;
import com.example.hotelmanagement.viewmodels.GuestViewModel;
import com.example.hotelmanagement.viewmodels.RentalFormViewModel;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;
import com.example.hotelmanagement.viewmodels.RoomViewModel;

public class ActivityMain extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BillViewModel billViewModel = new ViewModelProvider(this).get(BillViewModel.class);
        RoomViewModel roomViewModel = new ViewModelProvider(this).get(RoomViewModel.class);
        GuestViewModel guestViewModel = new ViewModelProvider(this).get(GuestViewModel.class);
        RoomKindViewModel roomKindViewModel = new ViewModelProvider(this).get(RoomKindViewModel.class);
        GuestKindViewModel guestKindViewModel = new ViewModelProvider(this).get(GuestKindViewModel.class);
        RentalFormViewModel rentalFormViewModel = new ViewModelProvider(this).get(RentalFormViewModel.class);

        billViewModel.loadData();
        roomViewModel.loadData();
        guestViewModel.loadData();
        roomKindViewModel.loadData();
        guestKindViewModel.loadData();
        rentalFormViewModel.loadData();
    }

}