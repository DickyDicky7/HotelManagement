package com.example.hotelmanagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.hotelmanagement.databinding.ActivityMainBinding;

import java.util.LinkedList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult
            (new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Process.killProcess(Process.myPid());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        List<String> permissions = new LinkedList<>();
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission);
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestPermissionLauncher.unregister();
        requestPermissionLauncher = null;
    }

}