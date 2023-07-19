package com.example.hotelmanagement.report.strategy.implementation;

import android.annotation.SuppressLint;
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
import com.example.hotelmanagement.databinding.ChartReportIncomeMonthBinding;
import com.example.hotelmanagement.databinding.ChartReportIncomeYearBinding;
import com.example.hotelmanagement.observable.implementation.BillObservable;
import com.example.hotelmanagement.report.processor.ReportProcessor;
import com.example.hotelmanagement.report.strategy.abstraction.ReportStrategy;
import com.example.hotelmanagement.viewmodel.implementation.BillViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportStrategyIncome extends ReportStrategy {

    @NonNull
    private final ChartReportIncomeYearBinding chartReportIncomeYearBinding;
    @NonNull
    private final ChartReportIncomeMonthBinding chartReportIncomeMonthBinding;
    //private static int repostCount = 0;
    private float maxIncome = 0F;
    private float sumIncome = 0F;

    public ReportStrategyIncome(@NonNull LayoutInflater LayoutInflater, @NonNull ViewGroup container, @NonNull FragmentActivity fragmentActivity, @NonNull LifecycleOwner LifecycleOwner) {
        super(fragmentActivity, LifecycleOwner);
        this.chartReportIncomeYearBinding
                = ChartReportIncomeYearBinding
                .inflate(LayoutInflater, container, false);
        this.chartReportIncomeMonthBinding
                = ChartReportIncomeMonthBinding
                .inflate(LayoutInflater, container, false);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View produceChartBy(@NonNull ReportProcessor.Month month, @NonNull String year) {
        BillViewModel billViewModel = new ViewModelProvider(fragmentActivity).get(BillViewModel.class);
        billViewModel.getModelState().observe(_LifecycleOwner_
                , updatedBillObservables -> {
                    //if (++repostCount <= 2) { return; }
                    sumIncome = 0F;
                    maxIncome = 0F;
                    int toYear = Integer.parseInt(year);
                    int textColor = fragmentActivity.getColor(R.color.white_100);
                    float textSize = 11F;
                    Typeface textFont = ResourcesCompat.getFont(fragmentActivity, R.font.outfit);
                    List<BarEntry> barEntries = new ArrayList<>();
                    ValueFormatter valueFormatter = new LargeValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            if (value < 10000F) {
                                return String.valueOf((int) value);
                            } else {
                                return super.getFormattedValue(value);
                            }
                        }
                    };
                    if (month.equals(ReportProcessor.Month.Non)) {
                        updatedBillObservables.stream().filter(billObservable -> billObservable.getCreatedAt().getYear() <= toYear)
                                .collect(Collectors.groupingBy(billObservable -> billObservable.getCreatedAt().getYear()))
                                .forEach(
                                        (_year_, billObservables) -> {
                                            float income = (float) billObservables.stream().map(BillObservable::getCost).mapToDouble(Double::doubleValue).sum();
                                            sumIncome += income;
                                            maxIncome = Math.max(income, maxIncome);
                                            barEntries.add(new BarEntry((float) _year_, income));
                                        });
                        barEntries.sort(new EntryXComparator());
                        BarDataSet barDataSet = new BarDataSet(barEntries, "Income Over Each Year");
                        barDataSet.setColor(fragmentActivity.getColor(R.color.blue_100));
                        barDataSet.setValueTextSize(textSize);
                        barDataSet.setValueTypeface(textFont);
                        barDataSet.setValueTextColor(textColor);
                        barDataSet.setValueFormatter(valueFormatter);
                        BarData barData = new BarData(barDataSet);
                        barData.setBarWidth(0.5F);
                        XAxis xAxis = chartReportIncomeYearBinding.chartIncomeYear.getXAxis();
                        xAxis.setYOffset(10F);
                        xAxis.setTypeface(textFont);
                        xAxis.setTextSize(textSize);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1F);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setValueFormatter(valueFormatter);
                        YAxis yAxisL = chartReportIncomeYearBinding.chartIncomeYear.getAxisLeft();
                        yAxisL.setEnabled(true);
                        yAxisL.setTypeface(textFont);
                        yAxisL.setTextSize(textSize);
                        yAxisL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisL.setTextColor(textColor);
                        yAxisL.setDrawGridLines(false);
                        yAxisL.setGranularity(50F);
                        yAxisL.setGranularityEnabled(true);
                        if (maxIncome % yAxisL.getGranularity() != 0F) {
                            maxIncome = ((maxIncome / yAxisL.getGranularity()) + 1F) * yAxisL.getGranularity();
                        }
                        yAxisL.setAxisMinimum(00000000F);
                        yAxisL.setAxisMaximum(maxIncome + yAxisL.getGranularity() * 2F);
                        yAxisL.setLabelCount((int) (maxIncome / yAxisL.getGranularity()) + 2);
                        yAxisL.setValueFormatter(valueFormatter);
                        YAxis yAxisR = chartReportIncomeYearBinding.chartIncomeYear.getAxisRight();
                        yAxisR.setEnabled(true);
                        yAxisR.setTypeface(textFont);
                        yAxisR.setTextSize(textSize);
                        yAxisR.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisR.setTextColor(textColor);
                        yAxisR.setDrawGridLines(false);
                        yAxisR.setGranularity(50f);
                        yAxisR.setGranularityEnabled(true);
                        yAxisR.setTextColor(Color.TRANSPARENT);
                        if (maxIncome % yAxisR.getGranularity() != 0F) {
                            maxIncome = ((maxIncome / yAxisR.getGranularity()) + 1F) * yAxisR.getGranularity();
                        }
                        yAxisR.setAxisMinimum(00000000F);
                        yAxisR.setAxisMaximum(maxIncome + yAxisR.getGranularity() * 2F);
                        yAxisR.setLabelCount((int) (maxIncome / yAxisR.getGranularity()) + 2);
                        yAxisR.setValueFormatter(valueFormatter);
                        Legend legend = chartReportIncomeYearBinding.chartIncomeYear.getLegend();
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportIncomeYearBinding.chartIncomeYear.getDescription().setEnabled(false);
                        chartReportIncomeYearBinding.chartIncomeYear.setBorderWidth(1F);
                        chartReportIncomeYearBinding.chartIncomeYear.setBorderColor(fragmentActivity.getColor(R.color.gray_100));
                        chartReportIncomeYearBinding.chartIncomeYear.setDrawBorders(true);
                        chartReportIncomeYearBinding.chartIncomeYear.setExtraBottomOffset(20F);
                        chartReportIncomeYearBinding.chartIncomeYear
                                .setData(barData);
                        int durationMillisecondsY = 1500;
                        chartReportIncomeYearBinding.chartIncomeYear
                                .animateY(
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                        chartReportIncomeYearBinding.chartIncomeYearTextViewValue.setText(String.format("%,d", (int) sumIncome)
                                .replace(",", ".")
                                .replace(" ", "0"));
                    } else {
                        Map<Integer, Float> data =
                                Arrays.stream(ReportProcessor.Month.values()).collect(Collectors.toList())
                                        .subList(1, ReportProcessor.Month.values().length)
                                        .stream()
                                        .filter(_month_ ->
                                                _month_.ordinal() <= month.ordinal())
                                        .collect(Collectors.toMap(ReportProcessor.Month::ordinal, _month_ -> 0F));
                        updatedBillObservables.stream().filter(billObservable -> billObservable.getCreatedAt().getYear() == toYear && billObservable.getCreatedAt().getMonthValue() <= month.ordinal())
                                .collect(Collectors.groupingBy(billObservable -> billObservable.getCreatedAt().getMonthValue()))
                                .forEach(
                                        (_month_, billObservables) -> {
                                            float income = (float) billObservables.stream().map(BillObservable::getCost).mapToDouble(Double::doubleValue).sum();
                                            data.put(_month_, income);
                                            sumIncome += income;
                                            maxIncome = Math.max(income, maxIncome);
                                        }
                                );
                        data.forEach((_month_, income) -> barEntries.add(new BarEntry((float) _month_, income)));
                        barEntries.sort(new EntryXComparator());
                        BarDataSet barDataSet = new BarDataSet(barEntries, "Income Over Each Month");
                        barDataSet.setColor(fragmentActivity.getColor(R.color.teal_500));
                        barDataSet.setValueTextSize(textSize);
                        barDataSet.setValueTypeface(textFont);
                        barDataSet.setValueTextColor(textColor);
                        barDataSet.setValueFormatter(valueFormatter);
                        BarData barData = new BarData(barDataSet);
                        barData.setBarWidth(0.5F);
                        XAxis xAxis = chartReportIncomeMonthBinding.chartIncomeMonth.getXAxis();
                        xAxis.setYOffset(10F);
                        xAxis.setTypeface(textFont);
                        xAxis.setTextSize(textSize);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1F);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setAxisMinimum(0);
                        xAxis.setAxisMaximum(data.size() + 1);
                        xAxis.setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                return value == 0F || value == data.size() + 1 ? "" : ReportProcessor.Month.values()[(int) value].toString();
                            }
                        });
                        YAxis yAxisL = chartReportIncomeMonthBinding.chartIncomeMonth.getAxisLeft();
                        yAxisL.setEnabled(true);
                        yAxisL.setTypeface(textFont);
                        yAxisL.setTextSize(textSize);
                        yAxisL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisL.setTextColor(textColor);
                        yAxisL.setDrawGridLines(false);
                        yAxisL.setGranularity(50F);
                        yAxisL.setGranularityEnabled(true);
                        if (maxIncome % yAxisL.getGranularity() != 0F) {
                            maxIncome = ((maxIncome / yAxisL.getGranularity()) + 1F) * yAxisL.getGranularity();
                        }
                        yAxisL.setAxisMinimum(00000000F);
                        yAxisL.setAxisMaximum(maxIncome + yAxisL.getGranularity() * 2F);
                        yAxisL.setLabelCount((int) (maxIncome / yAxisL.getGranularity()) + 2);
                        yAxisL.setValueFormatter(valueFormatter);
                        YAxis yAxisR = chartReportIncomeMonthBinding.chartIncomeMonth.getAxisRight();
                        yAxisR.setEnabled(true);
                        yAxisR.setTypeface(textFont);
                        yAxisR.setTextSize(textSize);
                        yAxisR.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisR.setTextColor(textColor);
                        yAxisR.setDrawGridLines(false);
                        yAxisR.setGranularity(50f);
                        yAxisR.setGranularityEnabled(true);
                        yAxisR.setTextColor(Color.TRANSPARENT);
                        if (maxIncome % yAxisR.getGranularity() != 0F) {
                            maxIncome = ((maxIncome / yAxisR.getGranularity()) + 1F) * yAxisR.getGranularity();
                        }
                        yAxisR.setAxisMinimum(00000000F);
                        yAxisR.setAxisMaximum(maxIncome + yAxisR.getGranularity() * 2F);
                        yAxisR.setLabelCount((int) (maxIncome / yAxisR.getGranularity()) + 2);
                        yAxisR.setValueFormatter(valueFormatter);
                        Legend legend = chartReportIncomeMonthBinding.chartIncomeMonth.getLegend();
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportIncomeMonthBinding.chartIncomeMonth.getDescription().setEnabled(false);
                        chartReportIncomeMonthBinding.chartIncomeMonth.setBorderWidth(1F);
                        chartReportIncomeMonthBinding.chartIncomeMonth.setBorderColor(fragmentActivity.getColor(R.color.gray_100));
                        chartReportIncomeMonthBinding.chartIncomeMonth.setDrawBorders(true);
                        chartReportIncomeMonthBinding.chartIncomeMonth.setExtraBottomOffset(20F);
                        chartReportIncomeMonthBinding.chartIncomeMonth
                                .setData(barData);
                        int durationMillisecondsY = 1500;
                        chartReportIncomeMonthBinding.chartIncomeMonth
                                .animateY(
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                        chartReportIncomeMonthBinding.chartIncomeMonthTextViewValue.setText(String.format("%,d", (int) sumIncome)
                                .replace(",", ".")
                                .replace(" ", "0"));
                    }
                });
        return month.equals(ReportProcessor.Month.Non) ? chartReportIncomeYearBinding.getRoot() : chartReportIncomeMonthBinding.getRoot();
    }

}
