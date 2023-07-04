package com.example.hotelmanagement.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class ExtendedAdapter<Item, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    @NonNull
    protected final List<Item> items;
    @NonNull
    protected final FragmentActivity fragmentActivity;

    public ExtendedAdapter(@NonNull FragmentActivity fragmentActivity) {
        super();
        this.items = new ArrayList<>();
        this.fragmentActivity = fragmentActivity;
    }

    public ExtendedAdapter(@NonNull FragmentActivity fragmentActivity, @NonNull List<Item> items) {
        super();
        this.items = items;
        this.fragmentActivity = fragmentActivity;
    }

    public void Fill(@NonNull List<Item> newItems) {
        int startIndex = 0;
        int itemNumber = newItems.size();
        items.addAll(newItems);
        notifyItemRangeInserted(startIndex, itemNumber);
    }

    public void Clear() {
        int startIndex = 0;
        int itemNumber = items.size();
        items.clear();
        notifyItemRangeRemoved(startIndex, itemNumber);
    }

    public void Insert(@NonNull Item item) {
        items.add(item);
        notifyItemInserted(items.indexOf(item));
    }

    public void Delete(@NonNull Item item) {
        if (items.contains(item)) {
            int index = items.indexOf(item);
            items.remove(item);
            notifyItemRemoved(index);
        }
    }

    public void Update(@NonNull Item item) {
        if (items.contains(item)) {
            int index = items.indexOf(item);
            items.set(index, item);
            notifyItemChanged(index);
        }
    }

    @NonNull
    public Boolean HasTheSame(@NonNull List<Item> otherItems) {
        return items.equals(otherItems);
    }

    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull VH holder, int position);

    @Override
    public int getItemCount() {
        return items.size();
    }

}
