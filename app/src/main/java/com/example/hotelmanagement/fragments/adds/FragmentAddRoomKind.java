package com.example.hotelmanagement.fragments.adds;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentAddRoomKindBinding;
import com.example.hotelmanagement.dialog.FailureDialogFragment;
import com.example.hotelmanagement.dialog.SuccessDialogFragment;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;

public class FragmentAddRoomKind extends Fragment {

    private FragmentAddRoomKindBinding binding;
    private RoomKindViewModel roomKindViewModel;
    private RoomKindObservable roomKindObservable;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    roomKindObservable.setImageURL(uri.toString());
                    binding.edtImage.setColorFilter(Color.TRANSPARENT);
                    Glide.with(this).load(uri).centerInside().into(binding.edtImage);
                    requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddRoomKindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindObservable = new RoomKindObservable();
        binding.setRoomKindObservable(roomKindObservable);
        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                roomKindViewModel.on3ErrorsCallback = apolloErrors -> apolloException -> cloudinaryErrorInfo -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (apolloErrors != null) {
                                FailureDialogFragment.newOne(getParentFragmentManager()
                                        , "FragmentAddRoomKind Failure", apolloErrors.get(0).getMessage());
                            }
                        });
                    }
                };
                roomKindViewModel.onSuccessCallback = () -> {
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            // Những task buộc phải chạy trên main thread thì gọi ở đây (thường liên quan đến UI)
                            // Ví dụ như navigation
                            // Cách 1:
                            // NavHostFragment.findNavController(this).popBackStack();
                            // Cách 2:
                            // requireActivity().onBackPressed();
                            // Hoặc set observable mới
                            roomKindObservable = new RoomKindObservable();
                            binding.setRoomKindObservable(roomKindObservable);
                            binding.edtImage.setColorFilter(Color.WHITE);
                            Glide.with(this).load(AppCompatResources.getDrawable(requireContext(),
                                    R.drawable.upload_image)).centerInside().into(binding.edtImage);
                            SuccessDialogFragment.newOne(getParentFragmentManager()
                                    , "FragmentAddRoomKind Success", "Added successfully");
                        });
                    }
                };
                roomKindViewModel.onFailureCallback = null;
                if (roomKindViewModel.checkObservable(roomKindObservable, requireContext())) {
                    roomKindViewModel.insert(roomKindObservable);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            View currentFocusView = requireActivity().getCurrentFocus();
            if (currentFocusView != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        binding.edtImage.setColorFilter(Color.WHITE);
        binding.edtImage.setOnClickListener(_view_ -> {
            pickVisualMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        binding.btnBack.setOnClickListener(_view_ -> NavHostFragment.findNavController(this).popBackStack());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        roomKindViewModel = null;
        roomKindObservable = null;
        pickVisualMediaLauncher.unregister();
        pickVisualMediaLauncher = null;
    }

}
