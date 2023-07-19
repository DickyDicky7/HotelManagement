package com.example.hotelmanagement.fragment.misc.implementation;

import android.animation.FloatEvaluator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.hotelmanagement.R;
import com.example.hotelmanagement.databinding.FragmentMiscReportBinding;
import com.example.hotelmanagement.report.processor.ReportProcessor;
import com.example.hotelmanagement.report.strategy.implementation.ReportStrategyFavourablyRentedRoomKind;
import com.example.hotelmanagement.report.strategy.implementation.ReportStrategyGuestKindDistribution;
import com.example.hotelmanagement.report.strategy.implementation.ReportStrategyIncome;
import com.example.hotelmanagement.report.strategy.implementation.ReportStrategyRoomKindDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FragmentMiscReport extends Fragment {

    private FragmentMiscReportBinding binding;
    private boolean isGone = false;
    @Nullable
    private String selectedMonth;
    @Nullable
    private String selectedYears;
    private ReportProcessor reportProcessor;
    private ReportStrategyIncome
            reportStrategyIncome;
    private ReportStrategyRoomKindDistribution
            reportStrategyRoomKindDistribution;
    private ReportStrategyGuestKindDistribution
            reportStrategyGuestKindDistribution;
    private ReportStrategyFavourablyRentedRoomKind
            reportStrategyFavourablyRentedRoomKind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        assert container != null;
        reportStrategyGuestKindDistribution = new ReportStrategyGuestKindDistribution
                (getLayoutInflater(), container, requireActivity(), getViewLifecycleOwner());
        reportStrategyIncome = new ReportStrategyIncome
                (getLayoutInflater(), container, requireActivity(), getViewLifecycleOwner());
        reportStrategyRoomKindDistribution = new ReportStrategyRoomKindDistribution
                (getLayoutInflater(), container, requireActivity(), getViewLifecycleOwner());
        reportStrategyFavourablyRentedRoomKind = new ReportStrategyFavourablyRentedRoomKind
                (getLayoutInflater(), container, requireActivity(), getViewLifecycleOwner());
        reportProcessor = new ReportProcessor(reportStrategyIncome);
        binding = FragmentMiscReportBinding.inflate(inflater, container, false);
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
        arrayAdapterYears.add("");
        arrayAdapterYears.addAll(ReportProcessor.years);

        ArrayAdapter<String> arrayAdapterMonth = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapterMonth.setDropDownViewResource(R.layout.item_spinner);
        binding.monthChoiceSpinner.setAdapter(arrayAdapterMonth);
        arrayAdapterMonth.add("");
        arrayAdapterMonth.addAll(Arrays.stream(ReportProcessor.Month.values()).map(ReportProcessor.Month::name).collect(Collectors.toList()));

        ArrayAdapter<String> arrayAdapterTypes = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, new ArrayList<>());
        arrayAdapterTypes.setDropDownViewResource(R.layout.item_spinner);
        binding.typesChoiceSpinner.setAdapter(arrayAdapterTypes);
        arrayAdapterTypes.add("");
        arrayAdapterTypes.addAll(Arrays.asList("Income Over Different Time Periods", "Room Kind Distribution Over Different Time Periods", "Guest Kind Distribution Over Different Time Periods", "Favourably Rented Room Kind Over Different Time Periods"));

        binding.yearsChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition() != 0) {
                    selectedYears = adapterView.getSelectedItem().toString();
                    produceChart();
                } else {
                    selectedYears = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedYears = null;
            }
        });

        binding.monthChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getSelectedItemPosition() != 0) {
                    selectedMonth = adapterView.getSelectedItem().toString();
                    produceChart();
                } else {
                    selectedMonth = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selectedMonth = null;
            }
        });

        binding.typesChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean shouldProducingChart = false;
                switch (adapterView.getSelectedItem().toString()) {

                    case "Income Over Different Time Periods": {
                        shouldProducingChart = true;
                        reportProcessor.replaceReportStrategy(reportStrategyIncome);
                    }
                    break;

                    case "Room Kind Distribution Over Different Time Periods": {
                        shouldProducingChart = true;
                        reportProcessor.replaceReportStrategy(reportStrategyRoomKindDistribution);
                    }
                    break;

                    case "Guest Kind Distribution Over Different Time Periods": {
                        shouldProducingChart = true;
                        reportProcessor.replaceReportStrategy(reportStrategyGuestKindDistribution);
                    }
                    break;

                    case "Favourably Rented Room Kind Over Different Time Periods": {
                        shouldProducingChart = true;
                        reportProcessor.replaceReportStrategy(reportStrategyFavourablyRentedRoomKind);
                    }
                    break;

                    default: {
                    }
                    break;

                }
                if (shouldProducingChart) {
                    produceChart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        //2. in specific year, all months (through each month) // LineChart
        //through each year up to the current year // LineChart

//Income
        //By all year
        //By month each year
//most used room kind
        //most guest kind

//        attachViewToLayout(reportProcessor.produceChart_(ReportProcessor.Month.Non, "2023"));


//binding.chartMainConstraintLayout.requestLayout();

//        Random random = new Random();
//        List<Pair<Integer, Integer>> list = new ArrayList<>();
////        lineChart.enableScroll();
//        for (int i = 0; i <= 11; ++i) {
//            list.add(new Pair<>(i, random.nextInt(50)));
//        }
//        List<Entry> entries = new ArrayList<>();
//        for (Pair<Integer, Integer> pair : list) {
//            entries.add(new Entry(pair.first, pair.second));
//        }
//        entries.sort(new EntryXComparator());
//
//
//        LineDataSet lineDataSet = new LineDataSet(entries, "Some Label");
//        lineDataSet.setColor(Color.YELLOW);
//        lineDataSet.setValueTextColor(Color.RED);
//        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        lineDataSet.setDrawFilled(true);
//        lineDataSet.setFillColor(Color.YELLOW);
////        lineDataSet.get
//        LineData lineData = new LineData(lineDataSet);
////lineData.setValueTextColor(Color.WHITE);
//        lineChart.setData(lineData);
//        lineChart.getXAxis().setTextColor(Color.WHITE);
//        lineChart.getAxisLeft().setTextColor(Color.WHITE);
//        lineChart.getAxisRight().setTextColor(Color.WHITE);
//
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(true);
//        xAxis.setGranularity(1f);
//        xAxis.setGranularityEnabled(true);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(months.subList(1, months.size() - 1)));
//
//        lineChart.invalidate();
//
//
//        List<PieEntry> pieEntries = new ArrayList<>();
//        for (int i = 0; i <= 11; ++i) {
//            pieEntries.add(new PieEntry(100f / 12f, i));
//        }
//        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Some LAbel");
//        PieData pieData = new PieData(pieDataSet);
//        pieChart.setData(pieData);
//        pieChart.invalidate();
//
//
//        List<BarEntry> barEntries = new ArrayList<>();
//        for (Pair<Integer, Integer> pair : list) {
//            barEntries.add(new BarEntry(pair.first, pair.second));
//        }
//        barEntries.sort(new EntryXComparator());
//        BarDataSet barDataSet = new BarDataSet(barEntries, "Some label");
//        BarData barData = new BarData(barDataSet);
//        barChart.setData(barData);
//        barChart.invalidate();


    }

    protected void produceChart() {
        if (selectedMonth != null && selectedYears != null) {
            binding.chartMainConstraintLayout.removeAllViews();
            attachViewToLayout(reportProcessor
                    .produceChart_(ReportProcessor.Month.valueOf(selectedMonth), selectedYears));
        }
    }

    protected void attachViewToLayout(@NonNull View view) {
        binding.chartMainConstraintLayout.addView(view);
        ConstraintLayout.LayoutParams LayoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        LayoutParams.topToTop = binding.chartMainConstraintLayout.getId();
        LayoutParams.endToEnd = binding.chartMainConstraintLayout.getId();
        LayoutParams.startToStart
                = binding.chartMainConstraintLayout.getId();
        LayoutParams.bottomToBottom
                = binding.chartMainConstraintLayout.getId();
        view.setLayoutParams(LayoutParams);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        selectedMonth = null;
        selectedYears = null;
        reportProcessor = null;
        reportStrategyIncome = null;
        reportStrategyRoomKindDistribution = null;
        reportStrategyGuestKindDistribution = null;
        reportStrategyFavourablyRentedRoomKind = null;
    }

}
