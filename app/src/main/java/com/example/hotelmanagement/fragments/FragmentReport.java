package com.example.hotelmanagement.fragments;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentReportBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FragmentReport extends Fragment {

    private FragmentReportBinding binding;
    private boolean isGone = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ValueAnimator hidesChoiceButtonHide = ValueAnimator.ofObject(new FloatEvaluator(), 90, 270);
        ValueAnimator hidesChoiceButtonShow = ValueAnimator.ofObject(new FloatEvaluator(), 270, 90);
        hidesChoiceButtonHide.setDuration(300);
        hidesChoiceButtonShow.setDuration(300);
        hidesChoiceButtonHide.addUpdateListener(valueAnimator -> binding.hidesChoiceButton.setRotation((float) valueAnimator.getAnimatedValue()));
        hidesChoiceButtonShow.addUpdateListener(valueAnimator -> binding.hidesChoiceButton.setRotation((float) valueAnimator.getAnimatedValue()));

        binding.hidesChoiceButton.setOnClickListener(_view_ -> {

            if (isGone) {

                isGone = false;

                binding.monthChoiceSpinner.setVisibility(View.VISIBLE);
                binding.typesChoiceSpinner.setVisibility(View.VISIBLE);
                binding.yearsChoiceSpinner.setVisibility(View.VISIBLE);

                binding.monthChoiceTextView.setVisibility(View.VISIBLE);
                binding.typesChoiceTextView.setVisibility(View.VISIBLE);
                binding.yearsChoiceTextView.setVisibility(View.VISIBLE);

                hidesChoiceButtonShow.start();

            } else {

                isGone = true;

                binding.monthChoiceSpinner.setVisibility(View.GONE);
                binding.typesChoiceSpinner.setVisibility(View.GONE);
                binding.yearsChoiceSpinner.setVisibility(View.GONE);

                binding.monthChoiceTextView.setVisibility(View.GONE);
                binding.typesChoiceTextView.setVisibility(View.GONE);
                binding.yearsChoiceTextView.setVisibility(View.GONE);

                hidesChoiceButtonHide.start();

            }

        });

        ArrayAdapter<String> arrayAdapterYears = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapterYears.setDropDownViewResource(R.layout.item_spinner);
        binding.yearsChoiceSpinner.setAdapter(arrayAdapterYears);
        int currentYear = Year.now().getValue();
        for (int year = 2000; year <= currentYear; ++year) {
            arrayAdapterYears.add(String.valueOf(year));
        }

        ArrayAdapter<String> arrayAdapterMonth = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapterMonth.setDropDownViewResource(R.layout.item_spinner);
        binding.monthChoiceSpinner.setAdapter(arrayAdapterMonth);
        List<String> months = Arrays.asList(
                "All",
                "Jan",
                "Feb",
                "Mar",
                "Apr",
                "May",
                "Jun",
                "Jul",
                "Aug",
                "Sep",
                "Oct",
                "Nov",
                "Dec"
        );
        arrayAdapterMonth.addAll(months);

        ArrayAdapter<String> arrayAdapterTypes = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapterTypes.setDropDownViewResource(R.layout.item_spinner);
        binding.typesChoiceSpinner.setAdapter(arrayAdapterTypes);
        arrayAdapterTypes.addAll(Arrays.asList("Income Over Different Time Periods", "Room Kind Distribution Over Different Time Periods", "Guest Kind Distribution Over Different Time Periods", "Favourably Rented Room Kind Over Different Time Periods"));

        //Income
        //in year (through each month) // BarChart
        //through each year up to the current year //BarChart

        //Room Kind Distribution
        //1. up to specific month and in specific year // PieChart
        //2. up to specific year, all months (through each month) // LineChart
        //through each year up to the current year // LineChart

        //Guest Kind Distribution
        //1. up to specific month and in specific year // PieChart
        //2. up to specific year, all months (through each month) // LineChart
        //through each year up to the current year // LineChart

        //Favourably Rented Room Kind
        //1. in specific month and in specific year // PieChart
        //2. in specific year, all months (through each month) // LineChart
        //3. in specific year, all months // PieChart
        //through each year up to the current year // LineChart

//Income
        //By all year
        //By month each year
//most used room kind
        //most guest kind

        BarChart barChart = new BarChart(requireContext());
        PieChart pieChart = new PieChart(requireContext());
        LineChart lineChart = new LineChart(requireContext());
        attachChartToLayout(lineChart);


//        attachChartToLayout(lineChart);


//binding.chartMainConstraintLayout.requestLayout();

        Random random = new Random();
        List<Pair<Integer, Integer>> list = new ArrayList<>();
//        lineChart.enableScroll();
        for (int i = 0; i <= 11; ++i) {
            list.add(new Pair<>(i, random.nextInt(50)));
        }
        List<Entry> entries = new ArrayList<>();
        for (Pair<Integer, Integer> pair : list) {
            entries.add(new Entry(pair.first, pair.second));
        }
        entries.sort(new EntryXComparator());


        LineDataSet lineDataSet = new LineDataSet(entries, "Some Label");
        lineDataSet.setColor(Color.YELLOW);
        lineDataSet.setValueTextColor(Color.RED);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.YELLOW);
//        lineDataSet.get
        LineData lineData = new LineData(lineDataSet);
//lineData.setValueTextColor(Color.WHITE);
        lineChart.setData(lineData);
        lineChart.getXAxis().setTextColor(Color.WHITE);
        lineChart.getAxisLeft().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setTextColor(Color.WHITE);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months.subList(1, months.size() - 1)));

        lineChart.invalidate();


        List<PieEntry> pieEntries = new ArrayList<>();
        for (int i = 0; i <= 11; ++i) {
            pieEntries.add(new PieEntry(100f / 12f, i));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Some LAbel");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();


        List<BarEntry> barEntries = new ArrayList<>();
        for (Pair<Integer, Integer> pair : list) {
            barEntries.add(new BarEntry(pair.first, pair.second));
        }
        barEntries.sort(new EntryXComparator());
        BarDataSet barDataSet = new BarDataSet(barEntries, "Some label");
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.invalidate();


    }

    protected void attachChartToLayout(@NonNull Chart<?> chart) {
        binding.chartMainConstraintLayout.addView(chart);
        ConstraintLayout.LayoutParams LayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        LayoutParams.topToTop = binding.chartMainConstraintLayout.getId();
        LayoutParams.endToEnd = binding.chartMainConstraintLayout.getId();
        LayoutParams.startToStart
                = binding.chartMainConstraintLayout.getId();
        LayoutParams.bottomToBottom
                = binding.chartMainConstraintLayout.getId();
        chart.setLayoutParams(LayoutParams);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
