package it.di.unipi.sam.noisyscanner;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import it.di.unipi.sam.noisyscanner.database.AppDatabase;
import it.di.unipi.sam.noisyscanner.database.RecordingDAO;

public class StatisticFragment extends Fragment implements AdapterView.OnItemSelectedListener, RecordingService.OnNewDataListener {
    private RecordingService.RecordingBinder recordingService = null;
    private final ServiceConnection recordingConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            recordingService = (RecordingService.RecordingBinder) iBinder;
            recordingService.setOnNewDataListener(StatisticFragment.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            recordingService = null;
        }
    };

    private static StatisticFragment INSTANCE = null;

    private BarChart chart;
    private Spinner spinner;
    private MaterialCardView cardView;
    private TextView dayhour;
    private TextView city;
    private TextView maxDb;

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

        chart = (BarChart) view.findViewById(R.id.chart);

        cardView = view.findViewById(R.id.prova);
        dayhour = view.findViewById(R.id.dayhour);
        city = view.findViewById(R.id.loudest_city);
        maxDb = view.findViewById(R.id.maxDb);

        refreshLoudestCity(context);

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
        xAxis.setTextSize(11);
        if (isDarkTheme()) xAxis.setTextColor(getColor(R.color.grey));

        chart.getAxisRight().setEnabled(false);

        chart.getAxisLeft().setTextSize(12);
        if (isDarkTheme()) chart.getAxisLeft().setTextColor(getColor(R.color.grey));

        chart.getLegend().setEnabled(true);
        chart.getLegend().setTextSize(15);
        if (isDarkTheme()) chart.getLegend().setTextColor(getColor(R.color.grey));


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

    private boolean isDarkTheme() {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
            case Configuration.UI_MODE_NIGHT_NO:
                return false;
        }

        return false;
    }

    private int getColor(int colorRes) {
        return ContextCompat.getColor(getContext(), colorRes);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);

        refreshChartData(cursor.getString(0));
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

        refreshLoudestCity(getContext());

        Cursor cursor = (Cursor) spinner.getSelectedItem();

        String selectedItem;
        if (cursor != null) {
            selectedItem = cursor.getString(0);

            if (selectedItem.equals(timestamp.substring(0, 4))) {
                refreshChartData(selectedItem);
            }
        } else {
            refreshChartData(timestamp.substring(0, 4));
        }
    }

    @Override
    public void onFail() {
        if (getActivity() == null) return;

        if (((MainActivity)getActivity()).getVisibility()) {
            NoAudioDialog noAudioDialog = new NoAudioDialog();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            noAudioDialog.show(fm, "No Audio");
        }
    }

    private void refreshLoudestCity(Context context) {
        SimpleCursorAdapter yearAdapter = new SimpleCursorAdapter(context,
                android.R.layout.simple_spinner_item,
                null,
                new String[] {"_id"},
                new int[] {android.R.id.text1},
                0);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(context);

            RecordingDAO.LoudestCity ld = db.recordingDAO().getLoudestDay();

            Cursor cur = db.recordingDAO().getAllYears();
            yearAdapter.changeCursor(cur);

            CharSequence maxDecibel = ld.decibel + " Db";
            cardView.post(() -> {
                dayhour.setText(ld.dayhour);
                city.setText(ld.city);
                maxDb.setText(maxDecibel);
            });
            spinner.post(() -> spinner.setAdapter(yearAdapter));

        }).start();
    }

    private void refreshChartData(String selectedItem) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(getContext());
            List<RecordingDAO.Result> results =  db.recordingDAO().getAvgPerMonth(selectedItem);

            List<BarEntry> entries = new ArrayList<>();

            for (RecordingDAO.Result result : results) {
                entries.add(new BarEntry(getMonthIndex(result.value), (float) result.decibel));
            }

            BarDataSet dataSet = new BarDataSet(entries, getText(R.string.decibelAvg).toString());

            dataSet.setColors(getColor(R.color.material_light_orange));
            dataSet.setValueTextSize(15);
            if (isDarkTheme()) dataSet.setValueTextColor(getColor(R.color.grey));

            BarData barData = new BarData(dataSet);

            chart.setData(barData);
            chart.post(() -> {
                chart.notifyDataSetChanged();
                chart.invalidate();
            });
        }).start();
    }
}