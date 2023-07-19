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
import com.example.hotelmanagement.databinding.ChartReportFavourablyRentedRoomKindMonthBinding;
import com.example.hotelmanagement.databinding.ChartReportFavourablyRentedRoomKindYearBinding;
import com.example.hotelmanagement.report.processor.ReportProcessor;
import com.example.hotelmanagement.report.strategy.abstraction.ReportStrategy;
import com.example.hotelmanagement.viewmodel.implementation.RentalFormViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RoomViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportStrategyFavourablyRentedRoomKind extends ReportStrategy {

    @NonNull
    private final ChartReportFavourablyRentedRoomKindYearBinding chartReportFavourablyRentedRoomKindYearBinding;
    @NonNull
    private final ChartReportFavourablyRentedRoomKindMonthBinding chartReportFavourablyRentedRoomKindMonthBinding;
    private int colorIndex = 0;
    private int maxNumberOfRentalForms = 0;
    private int sumNumberOfRentalForms = 0;

    public ReportStrategyFavourablyRentedRoomKind(@NonNull LayoutInflater LayoutInflater, @NonNull ViewGroup container, @NonNull FragmentActivity fragmentActivity, @NonNull LifecycleOwner LifecycleOwner) {
        super(fragmentActivity, LifecycleOwner);
        chartReportFavourablyRentedRoomKindYearBinding
                = ChartReportFavourablyRentedRoomKindYearBinding
                .inflate(LayoutInflater, container, false);
        chartReportFavourablyRentedRoomKindMonthBinding
                = ChartReportFavourablyRentedRoomKindMonthBinding
                .inflate(LayoutInflater, container, false);
    }

    @NonNull
    @Override
    public View produceChartBy(@NonNull ReportProcessor.Month month, @NonNull String year) {
        RoomViewModel roomViewModel = new ViewModelProvider(fragmentActivity).get(RoomViewModel.class);
        RoomKindViewModel roomKindViewModel = new ViewModelProvider(fragmentActivity).get(RoomKindViewModel.class);
        RentalFormViewModel rentalFormViewModel = new ViewModelProvider(fragmentActivity).get(RentalFormViewModel.class);
        rentalFormViewModel.getModelState().observe(_LifecycleOwner_,
                updatedRentalFormObservables -> {
                    colorIndex = 0;
                    maxNumberOfRentalForms = 0;
                    sumNumberOfRentalForms = 0;
                    int toYear = Integer.parseInt(year);
                    int textColor = fragmentActivity.getColor(R.color.white_100);
                    float textSize = 11F;
                    Typeface textFont = ResourcesCompat.getFont(fragmentActivity, R.font.outfit);
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
                    List<ILineDataSet> LineDataSets = new ArrayList<>();
                    if (month.equals(ReportProcessor.Month.Non)) {
                        updatedRentalFormObservables
                                .stream()
                                .filter(rentalFormObservable ->
                                        rentalFormObservable.getCreatedAt().getYear() <= toYear)
                                .collect(Collectors.groupingBy(rentalFormObservable -> roomViewModel.getObservable(rentalFormObservable.getRoomId()).getRoomKindId()))
                                .forEach(
                                        (roomKindId, rentalFormObservables) -> {
                                            List<Entry> entries = new ArrayList<>();
                                            sumNumberOfRentalForms += rentalFormObservables.size();
                                            rentalFormObservables
                                                    .stream()
                                                    .collect(Collectors.groupingBy(rentalFormObservable -> rentalFormObservable.getCreatedAt().getYear()))
                                                    .forEach((_year_, _rentalFormObservables_) -> {
                                                        maxNumberOfRentalForms =
                                                                Math.max(maxNumberOfRentalForms, _rentalFormObservables_.size());
                                                        entries.add(new Entry((float) _year_, (float) _rentalFormObservables_.size()));
                                                    });
                                            entries.sort(new EntryXComparator());
                                            LineDataSet LineDataSet = new LineDataSet(entries, roomKindViewModel.getRoomKindName(roomKindId));
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
                        XAxis xAxis = chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.getXAxis();
                        xAxis.setYOffset(10F);
                        xAxis.setTypeface(textFont);
                        xAxis.setTextSize(textSize);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1F);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setValueFormatter(valueFormatter);
                        YAxis yAxisL = chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.getAxisLeft();
                        yAxisL.setEnabled(true);
                        yAxisL.setTypeface(textFont);
                        yAxisL.setTextSize(textSize);
                        yAxisL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisL.setTextColor(textColor);
                        yAxisL.setDrawGridLines(false);
                        yAxisL.setGranularity(5F);
                        yAxisL.setGranularityEnabled(true);
                        if (maxNumberOfRentalForms % yAxisL.getGranularity() != 0F) {
                            maxNumberOfRentalForms = ((maxNumberOfRentalForms / (int) yAxisL.getGranularity()) + 1) * (int) yAxisL.getGranularity();
                        }
                        yAxisL.setAxisMinimum(000000000000000000000F);
                        yAxisL.setAxisMaximum(maxNumberOfRentalForms + yAxisL.getGranularity());
                        yAxisL.setLabelCount((int) (maxNumberOfRentalForms / yAxisL.getGranularity()) + 1);
                        yAxisL.setValueFormatter(valueFormatter);
                        YAxis yAxisR = chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.getAxisRight();
                        yAxisR.setEnabled(true);
                        yAxisR.setTypeface(textFont);
                        yAxisR.setTextSize(textSize);
                        yAxisR.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisR.setTextColor(textColor);
                        yAxisR.setDrawGridLines(false);
                        yAxisR.setGranularity(5F);
                        yAxisR.setGranularityEnabled(true);
                        yAxisR.setTextColor(Color.TRANSPARENT);
                        if (maxNumberOfRentalForms % yAxisR.getGranularity() != 0F) {
                            maxNumberOfRentalForms = ((maxNumberOfRentalForms / (int) yAxisR.getGranularity()) + 1) * (int) yAxisR.getGranularity();
                        }
                        yAxisR.setAxisMinimum(000000000000000000000F);
                        yAxisR.setAxisMaximum(maxNumberOfRentalForms + yAxisR.getGranularity());
                        yAxisR.setLabelCount((int) (maxNumberOfRentalForms / yAxisR.getGranularity()) + 1);
                        yAxisR.setValueFormatter(valueFormatter);
                        Legend legend = chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.getLegend();
                        //legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.getDescription().setEnabled(false);
                        chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.setBorderWidth(1F);
                        chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.setBorderColor(fragmentActivity.getColor(R.color.gray_100));
                        chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.setDrawBorders(true);
                        //chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.setExtraTopOffset(100F);
                        chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear.setExtraBottomOffset(20F);
                        chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear
                                .setData(LineData);
                        int durationMillisecondsY = 1500;
                        chartReportFavourablyRentedRoomKindYearBinding.chartFavourablyRentedRoomKindYear
                                .animateY(
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                    } else {
                        updatedRentalFormObservables
                                .stream()
                                .filter(rentalFormObservable ->
                                        rentalFormObservable.getCreatedAt().getYear() == toYear && rentalFormObservable.getCreatedAt().getMonthValue() <= month.ordinal())
                                .collect(Collectors.groupingBy(rentalFormObservable -> roomViewModel.getObservable(rentalFormObservable.getRoomId()).getRoomKindId()))
                                .forEach(
                                        (roomKindId, rentalFormObservables) -> {
                                            List<Entry> entries = new ArrayList<>();
                                            sumNumberOfRentalForms += rentalFormObservables.size();
                                            rentalFormObservables
                                                    .stream()
                                                    .collect(Collectors.groupingBy(rentalFormObservable -> rentalFormObservable.getCreatedAt().getMonthValue()))
                                                    .forEach((_month_, _rentalFormObservables_) -> {
                                                        maxNumberOfRentalForms =
                                                                Math.max(maxNumberOfRentalForms, _rentalFormObservables_.size());
                                                        entries.add(new Entry((float) _month_, (float) _rentalFormObservables_.size()));
                                                    });
                                            entries.sort(new EntryXComparator());
                                            LineDataSet LineDataSet = new LineDataSet(entries, roomKindViewModel.getRoomKindName(roomKindId));
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
                        XAxis xAxis = chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.getXAxis();
                        xAxis.setYOffset(10F);
                        xAxis.setTypeface(textFont);
                        xAxis.setTextSize(textSize);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1F);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setValueFormatter(valueFormatter);
                        YAxis yAxisL = chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.getAxisLeft();
                        yAxisL.setEnabled(true);
                        yAxisL.setTypeface(textFont);
                        yAxisL.setTextSize(textSize);
                        yAxisL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisL.setTextColor(textColor);
                        yAxisL.setDrawGridLines(false);
                        yAxisL.setGranularity(5F);
                        yAxisL.setGranularityEnabled(true);
                        if (maxNumberOfRentalForms % yAxisL.getGranularity() != 0F) {
                            maxNumberOfRentalForms = ((maxNumberOfRentalForms / (int) yAxisL.getGranularity()) + 1) * (int) yAxisL.getGranularity();
                        }
                        yAxisL.setAxisMinimum(000000000000000000000F);
                        yAxisL.setAxisMaximum(maxNumberOfRentalForms + yAxisL.getGranularity());
                        yAxisL.setLabelCount((int) (maxNumberOfRentalForms / yAxisL.getGranularity()) + 1);
                        yAxisL.setValueFormatter(valueFormatter);
                        YAxis yAxisR = chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.getAxisRight();
                        yAxisR.setEnabled(true);
                        yAxisR.setTypeface(textFont);
                        yAxisR.setTextSize(textSize);
                        yAxisR.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisR.setTextColor(textColor);
                        yAxisR.setDrawGridLines(false);
                        yAxisR.setGranularity(5F);
                        yAxisR.setGranularityEnabled(true);
                        yAxisR.setTextColor(Color.TRANSPARENT);
                        if (maxNumberOfRentalForms % yAxisR.getGranularity() != 0F) {
                            maxNumberOfRentalForms = ((maxNumberOfRentalForms / (int) yAxisR.getGranularity()) + 1) * (int) yAxisR.getGranularity();
                        }
                        yAxisR.setAxisMinimum(000000000000000000000F);
                        yAxisR.setAxisMaximum(maxNumberOfRentalForms + yAxisR.getGranularity());
                        yAxisR.setLabelCount((int) (maxNumberOfRentalForms / yAxisR.getGranularity()) + 1);
                        yAxisR.setValueFormatter(valueFormatter);
                        Legend legend = chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.getLegend();
                        //legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.getDescription().setEnabled(false);
                        chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.setBorderWidth(1F);
                        chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.setBorderColor(fragmentActivity.getColor(R.color.gray_100));
                        chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.setDrawBorders(true);
                        //chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.setExtraTopOffset(100F);
                        chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth.setExtraBottomOffset(20F);
                        chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth
                                .setData(LineData);
                        int durationMillisecondsY = 1500;
                        chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonth
                                .animateY(
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                    }
                    chartReportFavourablyRentedRoomKindMonthBinding.chartFavourablyRentedRoomKindMonthTextViewValue.setText(String.valueOf(sumNumberOfRentalForms));
                }
        );
        return month.equals(ReportProcessor.Month.Non) ? chartReportFavourablyRentedRoomKindYearBinding.getRoot() : chartReportFavourablyRentedRoomKindMonthBinding.getRoot();
    }


}
