package it.di.unipi.sam.noisyscanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
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
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.di.unipi.sam.noisyscanner.database.AppDatabase;
import it.di.unipi.sam.noisyscanner.database.Recording;
import it.di.unipi.sam.noisyscanner.database.RecordingDAO;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment implements AdapterView.OnItemSelectedListener, RecordingService.OnNewDataListener {
    private RecordingService.RecordingBinder recordingService = null;
    private final ServiceConnection recordingConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            recordingService = (RecordingService.RecordingBinder) iBinder;
            recordingService.setOnNewDataListner(StatisticFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            recordingService = null;
        }
    };

    private static StatisticFragment INSTANCE = null;

    private BarChart chart;
    private Spinner spinner;

    public static Fragment getInstance() {
        if (INSTANCE == null)
            INSTANCE = new StatisticFragment();

        return INSTANCE;
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

        context.bindService(new Intent(context, RecordingService.class), recordingConnection, Context.BIND_AUTO_CREATE);

        spinner = (Spinner) view.findViewById(R.id.yearSpinner);
        spinner.setOnItemSelectedListener(this);
        SimpleCursorAdapter yearAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_spinner_item,
                null,
                new String[] {"_id"},
                new int[] {android.R.id.text1});

        chart = (BarChart) view.findViewById(R.id.chart);
        MaterialCardView cardView = view.findViewById(R.id.prova);
        TextView dayhour = view.findViewById(R.id.dayhour);
        TextView city = view.findViewById(R.id.loudest_city);
        TextView maxDb = view.findViewById(R.id.maxDb);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(context);

            RecordingDAO.LoudestCity ld = db.recordingDAO().getLoudestDay();

            Cursor cur = db.recordingDAO().getAllYears();
            yearAdapter.changeCursor(cur);

            cardView.post(() -> {
               dayhour.setText(ld.dayhour);
               city.setText(ld.city);
               maxDb.setText(ld.decibel + " Db");
            });
            spinner.post(() -> spinner.setAdapter(yearAdapter));

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
            final String[] month = res.getStringArray(R.array.months);

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return month[Math.round(value)];
            }
        });
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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(getContext());
            List<RecordingDAO.Result> results =  db.recordingDAO().getAvgPerMonth(cursor.getString(0));

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

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    public void onNewData(double decibel, String timestamp, String city) {
        if (getActivity() == null) return;

        if (((MainActivity)getActivity()).getVisibility()) {
            ResultDialog resultDialog = new ResultDialog(decibel, timestamp, city);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            resultDialog.show(fm, "Result Dialog");
        }

//        BarChart barChart = requireView().findViewById(R.id.chart);
//        Spinner spi = requireView().findViewById(R.id.yearSpinner);
//
//        RecordingAdapter ra = (RecordingAdapter) rv.getAdapter();
//        new Thread(() -> {
//            List<Recording> recordings = AppDatabase.getDatabaseInstance(getContext()).recordingDAO().getRecentRecordings(maxLastRecordings);
//
//            rv.post(() -> {
//                if (ra != null) {
//                    ra.setRecordings(recordings);
//                    ra.notifyDataSetChanged();
//                }
//            });
//        }).start();

    }
}