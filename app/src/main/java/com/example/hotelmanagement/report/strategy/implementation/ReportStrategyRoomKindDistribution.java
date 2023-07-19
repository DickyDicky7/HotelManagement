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
import com.example.hotelmanagement.databinding.ChartReportRoomKindDistributionMonthBinding;
import com.example.hotelmanagement.databinding.ChartReportRoomKindDistributionYearBinding;
import com.example.hotelmanagement.observable.implementation.RoomObservable;
import com.example.hotelmanagement.report.processor.ReportProcessor;
import com.example.hotelmanagement.report.strategy.abstraction.ReportStrategy;
import com.example.hotelmanagement.viewmodel.implementation.RoomKindViewModel;
import com.example.hotelmanagement.viewmodel.implementation.RoomViewModel;
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

public class ReportStrategyRoomKindDistribution extends ReportStrategy {

    @NonNull
    private final ChartReportRoomKindDistributionYearBinding chartReportRoomKindDistributionYearBinding;
    @NonNull
    private final ChartReportRoomKindDistributionMonthBinding chartReportRoomKindDistributionMonthBinding;
    private int colorIndex = 0;
    private int maxNumberOfRooms = 0;
    private int sumNumberOfRooms = 0;

    public ReportStrategyRoomKindDistribution(@NonNull LayoutInflater LayoutInflater, @NonNull ViewGroup container, @NonNull FragmentActivity fragmentActivity, @NonNull LifecycleOwner LifecycleOwner) {
        super(fragmentActivity, LifecycleOwner);
        this.chartReportRoomKindDistributionYearBinding
                = ChartReportRoomKindDistributionYearBinding
                .inflate(LayoutInflater, container, false);
        this.chartReportRoomKindDistributionMonthBinding
                = ChartReportRoomKindDistributionMonthBinding
                .inflate(LayoutInflater, container, false);
    }

    @NonNull
    @Override
    public View produceChartBy(@NonNull ReportProcessor.Month month, @NonNull String year) {
        RoomKindViewModel roomKindViewModel = new ViewModelProvider(fragmentActivity).get(RoomKindViewModel.class);
        RoomViewModel roomViewModel = new ViewModelProvider(fragmentActivity).get(RoomViewModel.class);
        roomViewModel.getModelState().observe(_LifecycleOwner_,
                updatedRoomObservables -> {
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
                        maxNumberOfRooms = 0;
                        sumNumberOfRooms = 0;
                        updatedRoomObservables.stream().filter(roomObservable -> roomObservable.getCreatedAt().getYear() <= toYear)
                                .collect(Collectors.groupingBy(RoomObservable::getRoomKindId))
                                .forEach(
                                        (roomKindId, roomObservables) -> {
                                            List<Entry> entries = new ArrayList<>();
                                            sumNumberOfRooms += roomObservables.size();
                                            roomObservables
                                                    .stream()
                                                    .collect(Collectors.groupingBy(roomObservable -> roomObservable.getCreatedAt().getYear()))
                                                    .forEach((_year_, _roomObservables_) -> {
                                                        maxNumberOfRooms
                                                                = Math.max(maxNumberOfRooms, _roomObservables_.size());
                                                        entries.add(new Entry((float) _year_, (float) _roomObservables_.size()));
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
                        XAxis xAxis = chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.getXAxis();
                        xAxis.setYOffset(10F);
                        xAxis.setTypeface(textFont);
                        xAxis.setTextSize(textSize);
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setTextColor(textColor);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1F);
                        xAxis.setGranularityEnabled(true);
                        xAxis.setValueFormatter(valueFormatter);
                        YAxis yAxisL = chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.getAxisLeft();
                        yAxisL.setEnabled(true);
                        yAxisL.setTypeface(textFont);
                        yAxisL.setTextSize(textSize);
                        yAxisL.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisL.setTextColor(textColor);
                        yAxisL.setDrawGridLines(false);
                        yAxisL.setGranularity(5F);
                        yAxisL.setGranularityEnabled(true);
                        if (maxNumberOfRooms % yAxisL.getGranularity() != 0F) {
                            maxNumberOfRooms = ((maxNumberOfRooms / (int) yAxisL.getGranularity()) + 1) * (int) yAxisL.getGranularity();
                        }
                        yAxisL.setAxisMinimum(000000000000000F);
                        yAxisL.setAxisMaximum(maxNumberOfRooms + yAxisL.getGranularity());
                        yAxisL.setLabelCount((int) (maxNumberOfRooms / yAxisL.getGranularity()) + 1);
                        yAxisL.setValueFormatter(valueFormatter);
                        YAxis yAxisR = chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.getAxisRight();
                        yAxisR.setEnabled(true);
                        yAxisR.setTypeface(textFont);
                        yAxisR.setTextSize(textSize);
                        yAxisR.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                        yAxisR.setTextColor(textColor);
                        yAxisR.setDrawGridLines(false);
                        yAxisR.setGranularity(50f);
                        yAxisR.setGranularityEnabled(true);
                        yAxisR.setTextColor(Color.TRANSPARENT);
                        if (maxNumberOfRooms % yAxisR.getGranularity() != 0F) {
                            maxNumberOfRooms = ((maxNumberOfRooms / (int) yAxisR.getGranularity()) + 1) * (int) yAxisR.getGranularity();
                        }
                        yAxisR.setAxisMinimum(000000000000000F);
                        yAxisR.setAxisMaximum(maxNumberOfRooms + yAxisR.getGranularity());
                        yAxisR.setLabelCount((int) (maxNumberOfRooms / yAxisR.getGranularity()) + 1);
                        yAxisR.setValueFormatter(valueFormatter);
                        Legend legend = chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.getLegend();
                        //legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.getDescription().setEnabled(false);
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.setBorderWidth(1F);
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.setBorderColor(fragmentActivity.getColor(R.color.gray_100));
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.setDrawBorders(true);
                        //chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.setExtraTopOffset(100F);
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear.setExtraBottomOffset(20F);
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear
                                .setData(LineData);
                        int durationMillisecondsY = 1500;
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYear
                                .animateY(
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                        chartReportRoomKindDistributionYearBinding.chartRoomKindDistributionYearTextViewValue.setText(String.valueOf(sumNumberOfRooms));
                    } else {
                        List<PieEntry> pieEntries = new ArrayList<>();
                        List<RoomObservable> roomObservables = updatedRoomObservables
                                .stream()
                                .filter(roomObservable ->
                                        roomObservable.getCreatedAt().getYear() < toYear ||
                                                (roomObservable.getCreatedAt().getYear() == toYear &&
                                                        roomObservable.getCreatedAt().getMonthValue() <= month.ordinal())).collect(Collectors.toList());
                        roomObservables.stream()
                                .collect(Collectors.groupingBy(RoomObservable::getRoomKindId))
                                .forEach(
                                        (roomKindId, _roomObservables_) -> {
                                            float value = (float) _roomObservables_.size() / (float) roomObservables.size();
                                            pieEntries.add(new PieEntry(
                                                    value,
                                                    roomKindViewModel.getRoomKindName(roomKindId)));
                                        });
                        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Room Kind Distribution Until " + month.name() + ", " + year);
                        pieDataSet.setValueTextSize(textSize);
                        pieDataSet.setValueTypeface(textFont);
                        pieDataSet.setValueTextColor(textColor);
                        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
                        PieData pieData = new PieData(pieDataSet);
                        pieData.setValueFormatter(new PercentFormatter(chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth));
                        Legend legend = chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.getLegend();
                        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                        legend.setXOffset(20F);
                        legend.setYOffset(20F);
                        legend.setTypeface(textFont);
                        legend.setTextSize(textSize);
                        legend.setTextColor(textColor);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.getDescription().setEnabled(false);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.setUsePercentValues(true);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.setExtraLeftOffset(10F);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.setExtraRightOffset(10F);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.setExtraBottomOffset(10F);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.setHoleRadius(10F);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.setTransparentCircleRadius(20F);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth.setData(pieData);
                        int durationMillisecondsX = 1500;
                        int durationMillisecondsY = 1500;
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonth
                                .animateXY(
                                        durationMillisecondsX,
                                        durationMillisecondsY,
                                        Easing.EaseInOutQuad);
                        chartReportRoomKindDistributionMonthBinding.chartRoomKindDistributionMonthTextViewValue
                                .setText(String.valueOf(roomObservables.size()));
                    }
                });
        return month.equals(ReportProcessor.Month.Non) ? chartReportRoomKindDistributionYearBinding.getRoot() : chartReportRoomKindDistributionMonthBinding.getRoot();
    }

}
