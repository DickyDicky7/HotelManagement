package com.example.hotelmanagement.report.strategy.implementation;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.ChartReportGuestKindDistributionMonthBinding;
import com.example.hotelmanagement.databinding.ChartReportGuestKindDistributionYearBinding;
import com.example.hotelmanagement.observable.implementation.GuestObservable;
import com.example.hotelmanagement.report.processor.ReportProcessor;
import com.example.hotelmanagement.report.strategy.abstraction.ReportStrategy;
import com.example.hotelmanagement.viewmodel.implementation.GuestKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.GuestViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportStrategyGuestKindDistribution extends ReportStrategy {

    @NonNull
    private final ChartReportGuestKindDistributionYearBinding chartReportGuestKindDistributionYearBinding;
    @NonNull
    private final ChartReportGuestKindDistributionMonthBinding chartReportGuestKindDistributionMonthBinding;
    private int colorIndex = 0;
    private int maxNumberOfGuests = 0;
    private int sumNumberOfGuests = 0;

    public ReportStrategyGuestKindDistribution(@NonNull LayoutInflater LayoutInflater, @NonNull ViewGroup container, @NonNull FragmentActivity fragmentActivity, @NonNull LifecycleOwner LifecycleOwner) {
        super(fragmentActivity, LifecycleOwner);
        this.chartReportGuestKindDistributionYearBinding
                = ChartReportGuestKindDistributionYearBinding
                .inflate(LayoutInflater, container, false);
        this.chartReportGuestKindDistributionMonthBinding
                = ChartReportGuestKindDistributionMonthBinding
                .inflate(LayoutInflater, container, false);
    }

    @NonNull
    @Override
    public View produceChartBy(@NonNull ReportProcessor.Month month, @NonNull String year) {
        GuestKindViewModel guestKindViewModel = new ViewModelProvider(fragmentActivity).get(GuestKindViewModel.class);
        GuestViewModel guestViewModel = new ViewModelProvider(fragmentActivity).get(GuestViewModel.class);
        guestViewModel.getModelState().observe(_LifecycleOwner_,
                updatedGuestObservables -> {
                    int toYear = Integer.parseInt(year);
                    int textColor = fragmentActivity.getColor(R.color.white_100);
                    float textSize = 11F;
                    Typeface textFont = ResourcesCompat.getFont(fragmentActivity, R.font.outfit);
                    if (month.equals(ReportProcessor.Month.Non)) {
                        List<ILineDataSet> LineDataSets = new ArrayList<>();
                        ValueFormatter valueFormatter
                                = new LargeValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                if (value < 10000F) {
                                    return String.valueOf((int) value);
                                } else {
                                    return super.getFormattedValue(value);
                                }
                            }
                        };
                        colorIndex = 0;
                        maxNumberOfGuests = 0;
                        sumNumberOfGuests = 0;
                        updatedGuestObservables.stream().filter(guestObservable -> guestObservable.getUpdatedAt().getYear() <= toYear)
                                .collect(Collectors.groupingBy(GuestObservable::getGuestKindId))
                                .forEach(
                                        (guestKindId, guestObservables) -> {
                                            List<Entry> entries = new ArrayList<>();
                                            sumNumberOfGuests += guestObservables.size();
                                            guestObservables
                                                    .stream()
                                                    .collect(Collectors.groupingBy(guestObservable -> guestObservable.getUpdatedAt().getYear()))
                                                    .forEach((_year_, _guestObservables_) -> {
                                                        maxNumberOfGuests
                                                                = Math.max(maxNumberOfGuests, _guestObservables_.size());
                                                        entries.add(new Entry((float) _year_, (float) _guestObservables_.size()));
                                                    });
                                            entries.sort(new EntryXComparator());
                                            LineDataSet LineDataSet = new LineDataSet(entries, guestKindViewModel.getGuestKindName(guestKindId));
                                            LineDataSet.setValueTextSize(textSize);
                                            LineDataSet.setValueTypeface(textFont);
                                            LineDataSet.setValueTextColor(textColor);
                                            LineDataSet.setColor(ColorTemplate.PASTEL_COLORS[colorIndex]);
                                            LineDataSet.setCircleColor(ColorTemplate.PASTEL_COLORS[colorIndex]);
                                            LineDataSet.setCircleHoleColor(ColorTemplate.PASTEL_COLORS[colorIndex]);
                                            LineDataSets.add(LineDataSet);
                                            ++colorIndex;
                                        });
                        LineData LineData = new LineData(LineDataSets);
                        XAxis xAxis = chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.getXAxis();
                        xAxis.setYOffset(10F);
                        xAxis.setTypeface(textFont);
                        xAxis.setTextSize(textSize);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1F);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setValueFormatter(valueFormatter);
                        YAxis yAxisL = chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.getAxisLeft();
                        yAxisL.setEnabled(true);
                        yAxisL.setTypeface(textFont);
                        yAxisL.setTextSize(textSize);
                        yAxisL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisL.setTextColor(textColor);
                        yAxisL.setDrawGridLines(false);
                        yAxisL.setGranularity(5F);
                        yAxisL.setGranularityEnabled(true);
                        if (maxNumberOfGuests % yAxisL.getGranularity() != 0F) {
                            maxNumberOfGuests = ((maxNumberOfGuests / (int) yAxisL.getGranularity()) + 1) * (int) yAxisL.getGranularity();
                        }
                        yAxisL.setAxisMinimum(0000000000000000F);
                        yAxisL.setAxisMaximum(maxNumberOfGuests + yAxisL.getGranularity());
                        yAxisL.setLabelCount((int) (maxNumberOfGuests / yAxisL.getGranularity()) + 1);
                        yAxisL.setValueFormatter(valueFormatter);
                        YAxis yAxisR = chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.getAxisRight();
                        yAxisR.setEnabled(true);
                        yAxisR.setTypeface(textFont);
                        yAxisR.setTextSize(textSize);
                        yAxisR.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisR.setTextColor(textColor);
                        yAxisR.setDrawGridLines(false);
                        yAxisR.setGranularity(50f);
                        yAxisR.setGranularityEnabled(true);
                        yAxisR.setTextColor(Color.TRANSPARENT);
                        if (maxNumberOfGuests % yAxisR.getGranularity() != 0F) {
                            maxNumberOfGuests = ((maxNumberOfGuests / (int) yAxisR.getGranularity()) + 1) * (int) yAxisR.getGranularity();
                        }
                        yAxisR.setAxisMinimum(0000000000000000F);
                        yAxisR.setAxisMaximum(maxNumberOfGuests + yAxisR.getGranularity());
                        yAxisR.setLabelCount((int) (maxNumberOfGuests / yAxisR.getGranularity()) + 1);
                        yAxisR.setValueFormatter(valueFormatter);
                        Legend legend = chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.getLegend();
                        //legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.getDescription().setEnabled(false);
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.setBorderWidth(1F);
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.setBorderColor(fragmentActivity.getColor(R.color.gray_100));
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.setDrawBorders(true);
                        //chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.setExtraTopOffset(100F);
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear.setExtraBottomOffset(20F);
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear
                                .setData(LineData);
                        int durationMillisecondsY = 1500;
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYear
                                .animateY(
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                        chartReportGuestKindDistributionYearBinding.chartGuestKindDistributionYearTextViewValue.setText(String.valueOf(sumNumberOfGuests));
                    } else {
                        List<PieEntry> pieEntries = new ArrayList<>();
                        List<GuestObservable> guestObservables = updatedGuestObservables
                                .stream()
                                .filter(guestObservable ->
                                        guestObservable.getUpdatedAt().getYear() < toYear ||
                                                (guestObservable.getUpdatedAt().getYear() == toYear &&
                                                        guestObservable.getUpdatedAt().getMonthValue() <= month.ordinal())).collect(Collectors.toList());
                        guestObservables.stream()
                                .collect(Collectors.groupingBy(GuestObservable::getGuestKindId))
                                .forEach(
                                        (guestKindId, _guestObservables_) -> {
                                            float value = (float) _guestObservables_.size() / (float) guestObservables.size();
                                            pieEntries.add(new PieEntry(
                                                    value,
                                                    guestKindViewModel.getGuestKindName(guestKindId)));
                                        });
                        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Guest Kind Distribution Until " + month.name() + ", " + year);
                        pieDataSet.setValueTextSize(textSize);
                        pieDataSet.setValueTypeface(textFont);
                        pieDataSet.setValueTextColor(textColor);
                        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
                        PieData pieData = new PieData(pieDataSet);
                        pieData.setValueFormatter(new PercentFormatter(chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth));
                        Legend legend = chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.getLegend();
                        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                        legend.setXOffset(20F);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.getDescription().setEnabled(false);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.setUsePercentValues(true);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.setExtraLeftOffset(10F);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.setExtraRightOffset(10F);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.setExtraBottomOffset(10F);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.setHoleRadius(10F);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.setTransparentCircleRadius(20F);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth.setData(pieData);
                        int durationMillisecondsX = 1500;
                        int durationMillisecondsY = 1500;
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonth
                                .animateXY(
                                        durationMillisecondsX,
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                        chartReportGuestKindDistributionMonthBinding.chartGuestKindDistributionMonthTextViewValue
                                .setText(String.valueOf(guestObservables.size()));
                    }
                });
        return month.equals(ReportProcessor.Month.Non) ? chartReportGuestKindDistributionYearBinding.getRoot() : chartReportGuestKindDistributionMonthBinding.getRoot();
    }

}
