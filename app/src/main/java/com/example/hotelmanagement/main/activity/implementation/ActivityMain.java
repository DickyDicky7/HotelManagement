package com.example.hotelmanagement.main.activity.implementation;

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
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.ActivityMainBinding;
import com.example.hotelmanagement.dialog.watcher.DialogFragmentWatcher;

import java.util.Arrays;
import java.util.List;

public class ActivityMain extends AppCompatActivity {

    private ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher = registerForActivityResult
            (new ActivityResultContracts.RequestMultiplePermissions(), isGrantedResult -> {
                boolean notWantedValue = false;
                if (isGrantedResult.containsValue(notWantedValue)) {
                    Process.killProcess(Process.myPid());
                }
            });

    private GestureDetectorCompat gestureDetectorCompat;
    private NavController navController;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> _permissions = Arrays.asList(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE);
        requestMultiplePermissionsLauncher.launch(_permissions
                .stream()
                .filter(permission ->
                        ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED).toArray(String[]::new));

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
            if (navDestination.getId() == R.id.fragmentMiscHome) {
                showNavigationBar();
            } else {
                hideNavigationBar();
            }
        });

        binding.navigationBar.accountButton.setOnClickListener(_view_ -> {
            navController.navigate(R.id.action_fragmentMiscHome_to_fragmentMiscAccount);
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (navController.getCurrentDestination() != null) {
            if (navController.getCurrentDestination().getId() == R.id.fragmentMiscHome) {
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
        requestMultiplePermissionsLauncher.unregister();
        requestMultiplePermissionsLauncher = null;
    }

}
