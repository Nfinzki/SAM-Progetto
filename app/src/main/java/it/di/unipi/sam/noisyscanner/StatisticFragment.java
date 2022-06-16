package it.di.unipi.sam.noisyscanner;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.di.unipi.sam.noisyscanner.database.AppDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

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

        TextView loudestHour = view.findViewById(R.id.loudest_hour);
        TextView loudestDay = view.findViewById(R.id.loudest_day);
        TextView loudestMonth = view.findViewById(R.id.loudest_month);
        TextView loudestCity = view.findViewById(R.id.loudest_city);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabaseInstance(context);

            String lh = db.recordingDAO().getLoudestHour();
            String ld = db.recordingDAO().getLoudestDay();
            String lm = db.recordingDAO().getLoudestMonth();
            String lc = db.recordingDAO().getLoudestCity();

            loudestHour.post(() -> loudestHour.setText(lh));
            loudestDay.post(() -> loudestDay.setText(ld));
            loudestMonth.post(() -> loudestMonth.setText(lm));
            loudestCity.post(() -> loudestCity.setText(lc));
        }).start();
    }
}