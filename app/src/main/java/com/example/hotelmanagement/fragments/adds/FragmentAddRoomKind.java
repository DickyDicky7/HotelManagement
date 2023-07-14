package com.example.hotelmanagement.fragments.adds;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.hotelmanagement.common.Common;
import com.example.hotelmanagement.databinding.FragmentAddRoomKindBinding;
import com.example.hotelmanagement.dialog.watcher.DialogFragmentWatcher;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.popupwindow.implementation.PopupWindowLoading;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;

public class FragmentAddRoomKind extends Fragment {

    @Nullable
    private PopupWindowLoading popupWindowLoading;

    private FragmentAddRoomKindBinding binding;
    private RoomKindViewModel roomKindViewModel;
    private RoomKindObservable roomKindObservable;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("Local Image URI", uri.toString());
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
                    String failureTag = "FragmentAddRoomKind Failure";
                    DialogFragmentWatcher.dialogFragmentFailureSubscribe(failureTag, Common.getFailureMessage(
                            apolloErrors, apolloException, cloudinaryErrorInfo));
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            if (popupWindowLoading != null) {
                                popupWindowLoading.dismiss();
                            }
                        });
                    }
                };
                roomKindViewModel.onSuccessCallback = () -> {
                    String successTag = "FragmentAddRoomKind Success";
                    DialogFragmentWatcher.dialogFragmentSuccessSubscribe(successTag, Common.getSuccessMessage(
                            Common.Action.INSERT_ITEM));
                    if (getActivity() != null) {
                        requireActivity().runOnUiThread(() -> {
                            // Tasks that must run on the main thread are called here (usually related to UI)
                            // For example, navigation
                            // Method 1:
                            // NavHostFragment.findNavController(this).popBackStack();
                            // Method 2:
                            // requireActivity().onBackPressed();
                            // Or set a new observable
                            roomKindObservable = new RoomKindObservable();
                            binding.setRoomKindObservable(roomKindObservable);
                            binding.edtImage.setColorFilter(Color.WHITE);
                            Glide.with(this).load(AppCompatResources.getDrawable(requireContext(),
                                    R.drawable.upload_image)).centerInside().into(binding.edtImage);
                            if (popupWindowLoading != null) {
                                popupWindowLoading.dismiss();
                            }
                        });
                    }
                };
                roomKindViewModel.onFailureCallback = null;
                if (roomKindViewModel.checkObservable(
                        roomKindObservable,
                        requireContext(),
                        binding.getRoot())) {
                    roomKindViewModel.insert(roomKindObservable);
                    popupWindowLoading = PopupWindowLoading.newOne(getLayoutInflater(), binding.linearAddRoomKind);
                    popupWindowLoading.showAsDropDown
                            (binding.linearAddRoomKind);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Common.hideKeyboard(requireActivity());
        });

        binding.edtImage.setColorFilter(Color.WHITE);
        binding.edtImage.setOnClickListener(_view_ -> {
            Common.hideKeyboard(requireActivity());
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
        if (popupWindowLoading != null) {
            popupWindowLoading.dismiss();
        }
    }

}
