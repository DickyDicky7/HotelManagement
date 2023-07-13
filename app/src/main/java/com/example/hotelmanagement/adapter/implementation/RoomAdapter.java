package com.example.hotelmanagement.adapter.implementation;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.bumptech.glide.Glide;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.adapter.abstraction.ExtendedAdapter;
import com.example.hotelmanagement.adapter.viewholder.implementation.RoomViewHolder;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomBinding;
import com.example.hotelmanagement.databinding.RecyclerViewItemRoomOccupiedBinding;
import com.example.hotelmanagement.observables.RoomKindObservable;
import com.example.hotelmanagement.observables.RoomObservable;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("FieldCanBeLocal")
public class RoomAdapter extends ExtendedAdapter<RoomObservable, RoomViewHolder> {

    private Boolean isFstTime;
    private static Integer clickedId;
    private Drawable normalUserImage;
    private Drawable normalMarkImage;
    private int normalBackgroundColor;
    private int normalForegroundColor;
    private Drawable clickedUserImage;
    private int clickedBackgroundColor;
    private int clickedForegroundColor;
    private final RoomListener roomListener;

    private View clickedItem = null;
    private ImageView clickedItemUserImageView = null;
    private ImageView clickedItemMarkImageView = null;
    private TextView clickedItemRoomNameTextView = null;
    private TextView clickedItemRoomKindTextView = null;

    public RoomAdapter(FragmentActivity fragmentActivity, RoomListener roomListener) {
        super(fragmentActivity);
        this.isFstTime = true;
        this.roomListener = roomListener;
    }

    public RoomAdapter(FragmentActivity fragmentActivity, RoomListener roomListener, List<RoomObservable> roomObservables) {
        super(fragmentActivity, roomObservables);
        this.isFstTime = true;
        this.roomListener = roomListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            RecyclerViewItemRoomBinding binding = RecyclerViewItemRoomBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new RoomViewHolder(binding);
        }
        RecyclerViewItemRoomOccupiedBinding binding = RecyclerViewItemRoomOccupiedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RoomViewHolder(binding);
    }

    @Override
    @SuppressWarnings({"UnusedAssignment", "IfStatementWithIdenticalBranches"})
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {

        RoomObservable roomObservable = items.get(position);

        Integer clickedItemViewType = null;
        ViewBinding clickedItemBinding = null;

        RoomKindViewModel roomKindViewModel = new ViewModelProvider(fragmentActivity).get(RoomKindViewModel.class);
        List<RoomKindObservable> roomKindObservables = roomKindViewModel.getModelState().getValue();
        Optional<RoomKindObservable> optionalRoomKindObservable = Optional.empty();
        if (roomKindObservables != null) {
            optionalRoomKindObservable = roomKindObservables.stream().filter
                    (roomKindObservable -> roomKindObservable.getId().equals(roomObservable.getRoomKindId())).findFirst();
        }

        if (holder.getItemViewType() == 0) {
            RecyclerViewItemRoomBinding binding = RecyclerViewItemRoomBinding.bind(holder.itemView);
            binding.itemRoomRoomNameTextView.setText(roomObservable.getName());
            if (optionalRoomKindObservable.isPresent()) {
                RoomKindObservable roomKindObservable = optionalRoomKindObservable.get();
                binding.itemRoomRoomKindTextView.setText(roomKindObservable.getName());
            } else {
                binding.itemRoomRoomKindTextView.setText("");
            }

            binding.itemRoom.setOnClickListener(view -> {
                roomListener.onRoomClick(roomObservable);
                setOnClickedUI(roomObservable.getId(), holder.getItemViewType(), binding);
            });

            clickedItemBinding = binding;
            clickedItemViewType = holder.getItemViewType();

        } else {
            RecyclerViewItemRoomOccupiedBinding binding = RecyclerViewItemRoomOccupiedBinding.bind(holder.itemView);
            binding.itemRoomRoomNameTextView.setText(roomObservable.getName());
            if (optionalRoomKindObservable.isPresent()) {
                RoomKindObservable roomKindObservable = optionalRoomKindObservable.get();
                binding.itemRoomRoomKindTextView.setText(roomKindObservable.getName());
            } else {
                binding.itemRoomRoomKindTextView.setText("");
            }

            binding.itemRoom.setOnClickListener(view -> {
                roomListener.onRoomClick(roomObservable);
                setOnClickedUI(roomObservable.getId(), holder.getItemViewType(), binding);
            });

            clickedItemBinding = binding;
            clickedItemViewType = holder.getItemViewType();

        }

        if (roomObservable.getId().equals(clickedId)) {
            roomListener.onRoomClick(roomObservable);
            setOnClickedUI(roomObservable.getId(), clickedItemViewType, clickedItemBinding);
        }

    }

    @SuppressWarnings("ConstantConditions")
    public void setOnClickedUI(@NonNull Integer itemId, @NonNull Integer itemViewType, @NonNull ViewBinding binding) {

        if (clickedItem != null) {
            clickedItem.getBackground().setColorFilter(normalBackgroundColor, PorterDuff.Mode.SRC_IN);
        }
        if (clickedItemRoomNameTextView != null) {
            clickedItemRoomNameTextView.setTextColor(normalForegroundColor);
        }
        if (clickedItemRoomKindTextView != null) {
            clickedItemRoomKindTextView.setTextColor(normalForegroundColor);
        }
        if (clickedItemUserImageView != null) {
            Glide.with(fragmentActivity).load(normalUserImage).fitCenter().into(clickedItemUserImageView);
        }
        if (clickedItemMarkImageView != null) {
            clickedItemMarkImageView.setColorFilter(normalForegroundColor);
            Glide.with(fragmentActivity).load(normalMarkImage).fitCenter().into(clickedItemMarkImageView);
        }

        if (itemId.equals(clickedId)) {
            if (isFstTime) {
                isFstTime = false;
            } else {
                isFstTime = false;
                clickedId = null;
                return;
            }
        } else {
            isFstTime = false;
            clickedId = itemId;
        }

        normalMarkImage = AppCompatResources.getDrawable(fragmentActivity, R.drawable.img_checkmark);
        normalUserImage = AppCompatResources.getDrawable(fragmentActivity,
                itemViewType == 0 ? R.drawable.img_double_users_i :
                        itemViewType == 1 ? R.drawable.img_double_users_g : 0);

        normalForegroundColor = fragmentActivity.getColor
                (itemViewType == 0 ? R.color.indigo_200 :
                        itemViewType == 1 ? R.color.green_300 : 0);
        normalBackgroundColor = fragmentActivity.getColor
                (itemViewType == 0 ? R.color.blue_gray_300 :
                        itemViewType == 1 ? R.color.blue_gray_100 : 0);

        clickedBackgroundColor = fragmentActivity.getColor(R.color.indigo_100);
        clickedForegroundColor = normalBackgroundColor;
        clickedUserImage = AppCompatResources.getDrawable(fragmentActivity, R.drawable.img_double_users_w);

        if (itemViewType == 0) {
            RecyclerViewItemRoomBinding _binding_ = (RecyclerViewItemRoomBinding) binding;
            clickedItem = _binding_.itemRoom;
            clickedItemMarkImageView = null;
            clickedItemUserImageView = _binding_.itemRoomUserImageView;
            clickedItemRoomNameTextView = _binding_.itemRoomRoomNameTextView;
            clickedItemRoomKindTextView = _binding_.itemRoomRoomKindTextView;
        } else {
            RecyclerViewItemRoomOccupiedBinding _binding_ = (RecyclerViewItemRoomOccupiedBinding) binding;
            clickedItem = _binding_.itemRoom;
            clickedItemMarkImageView = _binding_.itemRoomMarkImageView;
            clickedItemUserImageView = _binding_.itemRoomUserImageView;
            clickedItemRoomNameTextView = _binding_.itemRoomRoomNameTextView;
            clickedItemRoomKindTextView = _binding_.itemRoomRoomKindTextView;
        }

        if (clickedItem != null) {
            clickedItem.getBackground().setColorFilter(clickedBackgroundColor, PorterDuff.Mode.SRC_IN);
        }
        if (clickedItemRoomNameTextView != null) {
            clickedItemRoomNameTextView.setTextColor(clickedForegroundColor);
        }
        if (clickedItemRoomKindTextView != null) {
            clickedItemRoomKindTextView.setTextColor(clickedForegroundColor);
        }
        if (clickedItemUserImageView != null) {
            Glide.with(fragmentActivity).load(clickedUserImage).fitCenter().into(clickedItemUserImageView);
        }
        if (clickedItemMarkImageView != null) {
            clickedItemMarkImageView.setColorFilter(clickedForegroundColor);
        }

    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getIsOccupied() ? 1 : 0;
    }

    public interface RoomListener {
        void onRoomClick(RoomObservable roomObservable);
    }

}
