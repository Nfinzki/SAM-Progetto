package it.di.unipi.sam.noisyscanner;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
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
    private LineChart chart;

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

        chart = (LineChart) view.findViewById(R.id.chart);

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

        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM", Locale.ENGLISH);

            @Override
            public String getFormattedValue(float value) {
                long millis = (long) value * 1000L;
                return mFormat.format(new Date(millis));
            }
        });

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1, 1));
        entries.add(new Entry(2, (float) 3.4));
        entries.add(new Entry(3, (float) 4.2));
        entries.add(new Entry(4, (float) 7.1));
        entries.add(new Entry(5, (float) 6.4));

        LineDataSet dataSet = new LineDataSet(entries, "Dati di prova");

        LineData lineData = new LineData(dataSet);

        chart.setData(lineData);
        chart.invalidate();
    }
}