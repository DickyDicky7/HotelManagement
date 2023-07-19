package com.example.hotelmanagement.report.strategy.abstraction;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import com.example.hotelmanagement.report.processor.ReportProcessor;

public abstract class ReportStrategy {

    @NonNull
    protected LifecycleOwner _LifecycleOwner_;
    @NonNull
    protected FragmentActivity fragmentActivity;

    public ReportStrategy(@NonNull FragmentActivity fragmentActivity, @NonNull LifecycleOwner _LifecycleOwner_) {
        this._LifecycleOwner_ = _LifecycleOwner_;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    public abstract View produceChartBy(@NonNull ReportProcessor.Month month, @NonNull String year);

}
