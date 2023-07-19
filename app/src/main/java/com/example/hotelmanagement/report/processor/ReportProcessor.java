package com.example.hotelmanagement.report.processor;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.hotelmanagement.report.strategy.abstraction.ReportStrategy;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class ReportProcessor {

    @NonNull
    public static final List<String> years = new ArrayList<>();
    @NonNull
    protected ReportStrategy reportStrategy;

    public ReportProcessor(@NonNull ReportStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
        if (years.isEmpty()) {
            addAllYearsUpToTheCurrentYearFromYear(2000);
        }
    }

    public static void addAllYearsUpToTheCurrentYearFromYear(int startYear) {
        int currentYear = Year.now().getValue();
        for (int year = currentYear; year >= startYear; --year) {
            years.add(String.valueOf(year));
        }
    }

    @NonNull
    public View produceChart_(@NonNull Month month, @NonNull String year) {
        return reportStrategy.produceChartBy(month, year);
    }

    public void replaceReportStrategy(@NonNull ReportStrategy reportStrategy) {
        this.reportStrategy = reportStrategy;
    }

    public enum Month {
        Non,
        Jan,
        Feb,
        Mar,
        Apr,
        May,
        Jun,
        Jul,
        Aug,
        Sep,
        Oct,
        Nov,
        Dec,
    }

}
