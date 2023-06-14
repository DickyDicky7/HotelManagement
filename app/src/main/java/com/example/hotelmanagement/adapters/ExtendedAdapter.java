package com.example.hotelmanagement.adapters;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public abstract class ExtendedAdapter<BO extends BaseObservable, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final List<BO> baseObservables;
    protected final FragmentActivity fragmentActivity;

    public ExtendedAdapter(FragmentActivity fragmentActivity) {
        super();
        this.baseObservables = new LinkedList<BO>();
        this.fragmentActivity = fragmentActivity;
    }

    public ExtendedAdapter(FragmentActivity fragmentActivity, List<BO> baseObservables) {
        super();
        this.baseObservables = baseObservables;
        this.fragmentActivity = fragmentActivity;
    }

    public void Fill(@NonNull List<BO> newBaseObservables) {
        if (baseObservables != null) {
            int startIndex = 0;
            int itemNumber = newBaseObservables.size();
            baseObservables.addAll(newBaseObservables);
            notifyItemRangeInserted(startIndex, itemNumber);
        }
    }

    public void Clear() {
        if (baseObservables != null) {
            int startIndex = 0;
            int itemNumber = baseObservables.size();
            baseObservables.clear();
            notifyItemRangeRemoved(startIndex, itemNumber);
        }
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
