package com.example.hotelmanagement.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public abstract class ExtendedAdapter<BO extends BaseObservable, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final List<BO> baseObservables;

    public ExtendedAdapter() {
        super();
        baseObservables = new LinkedList<BO>();
    }

    public void Insert(@NonNull BO baseObservable) {
        if (baseObservables != null) {
            baseObservables.add(baseObservable);
            notifyItemInserted(baseObservables.indexOf(baseObservable));
        }
    }

    public void Delete(@NonNull BO baseObservable) {
        if (baseObservables != null && baseObservables.contains(baseObservable)) {
            int index = baseObservables.indexOf(baseObservable);
            baseObservables.remove(baseObservable);
            notifyItemRemoved(index);
        }
    }

    public void Update(@NonNull BO baseObservable) {
        if (baseObservables != null && baseObservables.contains(baseObservable)) {
            int index = baseObservables.indexOf(baseObservable);
            baseObservables.set(index, baseObservable);
            notifyItemChanged(index);
        }
    }

    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(@NonNull VH holder, int position);

    @Override
    public int getItemCount() {
        return baseObservables != null ? baseObservables.size() : 0;
    }

}
