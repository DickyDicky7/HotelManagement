package com.example.hotelmanagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.hotelmanagement.databinding.ActivityMainBinding;
import com.example.hotelmanagement.dialogs.DialogFragmentWatcher;

import java.util.LinkedList;
import java.util.List;

public class ActivityMain extends AppCompatActivity {

    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult
            (new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Process.killProcess(Process.myPid());
                }
            });

    private GestureDetectorCompat gestureDetectorCompat;
    private NavController navController;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> permissions = new LinkedList<>();
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission);
            }
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.navConstraintLayout.setVisibility(View.INVISIBLE);
        navController = ((NavHostFragment) binding.mainNavHostFragment.getFragment()).getNavController();

        gestureDetectorCompat = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                float angle = (float) Math.toDegrees(Math.atan2(e1.getY() - e2.getY(), e2.getX() - e1.getX()));
                if (angle < -45.0f && angle >= -135.0f) {
                    hideNavigationBar();
                }
                if (angle > +45.0f && angle <= +135.0f) {
                    showNavigationBar();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        navController.addOnDestinationChangedListener((navController, navDestination, bundle) -> {
            if (navDestination.getId() == R.id.fragmentHome) {
                showNavigationBar();
            } else {
                hideNavigationBar();
            }
        });

        binding.navigationBar.accountButton.setOnClickListener(_view_ -> {
            navController.navigate(R.id.action_fragmentHome_to_fragmentAccount);
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (navController.getCurrentDestination() != null) {
            if (navController.getCurrentDestination().getId() == R.id.fragmentHome) {
                gestureDetectorCompat.onTouchEvent(event);
            }
        }
        return super.onTouchEvent(event);
    }

    public void showNavigationBar() {
        if (binding.navConstraintLayout.getVisibility() != View.VISIBLE) {
            YoYo.with(Techniques.SlideInUp).duration(300).onStart
                    (animator -> binding.navConstraintLayout.setVisibility(View.VISIBLE)).playOn(binding.navConstraintLayout);
        }
    }

    public void hideNavigationBar() {
        if (binding.navConstraintLayout.getVisibility() != View.INVISIBLE) {
            YoYo.with(Techniques.SlideOutDown).duration(300).onEnd
                    (animator -> binding.navConstraintLayout.setVisibility(View.INVISIBLE)).playOn(binding.navConstraintLayout);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DialogFragmentWatcher.watcher == null) {
            DialogFragmentWatcher.destroyed.set(false);
            DialogFragmentWatcher.processDialogFragmentSubscriptions(binding.mainNavHostFragment.getFragment().getParentFragmentManager());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DialogFragmentWatcher.destroyed.set(true);
        while (DialogFragmentWatcher.watcher != null && DialogFragmentWatcher.watcher.isAlive()) {
            Log.i("DialogFragmentWatcher", "Still Alive");
        }

        DialogFragmentWatcher.watcher = null;
        gestureDetectorCompat = null;
        navController = null;
        binding = null;
        requestPermissionLauncher.unregister();
        requestPermissionLauncher = null;
    }

}
