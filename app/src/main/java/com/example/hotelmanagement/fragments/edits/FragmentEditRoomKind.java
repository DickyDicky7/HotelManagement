package com.example.hotelmanagement.fragments.edits;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentEditRoomKindBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.viewmodels.RoomKindViewModel;

public class FragmentEditRoomKind extends Fragment {

    private FragmentEditRoomKindBinding binding;
    private RoomKindViewModel roomKindViewModel;
    private RoomKindObservable roomKindObservable;
    private ActivityResultLauncher<PickVisualMediaRequest> pickVisualMediaLauncher =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    System.out.println("uri : " + uri);
                    roomKindObservable.setImageURL(uri.toString());
                    binding.edtImage.setColorFilter(Color.TRANSPARENT);
                    Glide.with(this).load(uri).centerInside().into(binding.edtImage);
                    requireContext().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            });
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditRoomKindBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        roomKindViewModel = new ViewModelProvider(requireActivity()).get(RoomKindViewModel.class);
        roomKindObservable = new RoomKindObservable();
        int id = getArguments().getInt("id");
        System.out.println(id);
        roomKindViewModel.filldata(roomKindObservable, id);
        while (roomKindObservable.getImageURL() == null);
        System.out.println(roomKindObservable.getImageURL());
        Glide.with(this).load(roomKindObservable.getImageURL()).centerInside().into(binding.edtImage);

        binding.setRoomKindObservable(roomKindObservable);
        binding.btnDone.setOnClickListener(_view_ -> {
            try {
                roomKindViewModel.onSuccessCallback = () -> {
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
                        Glide.with(this).load(getResources().getDrawable(R.drawable.upload_image)).centerInside().into(binding.edtImage);
                    });
                };
                roomKindViewModel.onFailureCallback = null;
                if (roomKindViewModel.checkObservable(roomKindObservable, requireContext())) {
                    roomKindViewModel.update(roomKindObservable,id);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

//        binding.edtImage.setColorFilter(Color.WHITE);
        binding.edtImage.setOnClickListener(_view_ -> {
            pickVisualMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
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