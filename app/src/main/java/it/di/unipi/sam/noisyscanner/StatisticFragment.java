package it.di.unipi.sam.noisyscanner;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.di.unipi.sam.noisyscanner.database.AppDatabase;
import it.di.unipi.sam.noisyscanner.database.RecordingDAO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {
    private BarChart chart;

    public static Fragment newInstance() {
        StatisticFragment sf = new StatisticFragment();
        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Context context = view.getContext();

        chart = (BarChart) view.findViewById(R.id.chart);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.statistic_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        StatisticsAdapter adapter = new StatisticsAdapter();
        recyclerView.setAdapter(adapter);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(context);

            RecordingDAO.Result lh = db.recordingDAO().getLoudestHour();
            RecordingDAO.Result ld = db.recordingDAO().getLoudestDay();
            RecordingDAO.Result lm = db.recordingDAO().getLoudestMonth();
            RecordingDAO.Result lc = db.recordingDAO().getLoudestCity();

            adapter.setData(lh, ld, lm, lc);
            recyclerView.post(() -> adapter.notifyDataSetChanged());
        }).start();

        configureChart();
    }

    private void configureChart() {
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        chart.setDoubleTapToZoomEnabled(false);
        chart.setHighlightPerDragEnabled(false);
        chart.setHighlightPerTapEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);

        YAxis yAxis = chart.getAxisRight();
        yAxis.setEnabled(false);

        chart.getLegend().setEnabled(false);

        Resources res = getResources();

        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

            final String[] month = res.getStringArray(R.array.months);

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return month[Math.round(value)];
            }
        });

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(getContext());
            List<RecordingDAO.Result> results =  db.recordingDAO().getAvgPerMonth("2022");

            List<BarEntry> entries = new ArrayList<>();

            for (RecordingDAO.Result result : results) {
                entries.add(new BarEntry(getMonthIndex(result.value), (float) result.decibel));
            }

            BarDataSet dataSet = new BarDataSet(entries, "Dati di prova");


            BarData barData = new BarData(dataSet);

            chart.setData(barData);
            chart.post(() -> chart.invalidate());
        }).start();

    }

    private int getMonthIndex(String month) {
        switch (month) {
            case "01": return 0;
            case "02": return 1;
            case "03": return 2;
            case "04": return 3;
            case "05": return 4;
            case "06": return 5;
            case "07": return 6;
            case "08": return 7;
            case "09": return 8;
            case "10": return 9;
            case "11": return 10;
            case "12": return 11;
        }

        return 0;
    }
}