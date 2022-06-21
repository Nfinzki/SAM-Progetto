package it.di.unipi.sam.noisyscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.di.unipi.sam.noisyscanner.database.RecordingDAO;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticViewHolder> {
    private RecordingDAO.Result loudestHour = null;
    private RecordingDAO.Result loudestDay = null;
    private RecordingDAO.Result loudestMonth = null;
    private RecordingDAO.Result loudestCity = null;

    private final int numStatistics = 4;

    private Context context = null;

    public void setData(RecordingDAO.Result loudestHour, RecordingDAO.Result loudestDay, RecordingDAO.Result loudestMonth, RecordingDAO.Result loudestCity) {
        this.loudestHour = loudestHour;
        this.loudestDay = loudestDay;
        this.loudestMonth = loudestMonth;
        this.loudestCity = loudestCity;
    }

    @NonNull
    @Override
    public StatisticsAdapter.StatisticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_item, parent, false);
        context = view.getContext();
        return new StatisticsAdapter.StatisticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.StatisticViewHolder holder, int position) {
        CharSequence title = null;
        String value = null;
        String maxDecibel = null;

        switch (position) {
            case 0:
                title = context.getText(R.string.loudest_hour);
                value = loudestHour.value;
                maxDecibel = loudestHour.decibel + " Db";
                break;
            case 1:
                title = context.getText(R.string.loudest_day);
                value = loudestDay.value;
                maxDecibel = loudestDay.decibel + " Db";
                break;
            case 2:
                title = context.getText(R.string.loudest_month);
                value = loudestMonth.value;
                maxDecibel = loudestMonth.decibel + " Db";
                break;
            case 3:
                title = context.getText(R.string.loudest_city);
                value = loudestCity.value;
                maxDecibel = loudestCity.decibel + " Db";
                break;
        }

        holder.title.setText(title);
        holder.value.setText(value);
        holder.maxDecibel.setText(maxDecibel);
    }

    @Override
    public int getItemCount() {
        if (loudestHour != null && loudestDay != null && loudestMonth != null && loudestCity != null)
            return numStatistics;
        else
            return 0;
    }

    public static class StatisticViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView value;
        TextView maxDecibel;

        StatisticViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            value = (TextView) itemView.findViewById(R.id.value);
            maxDecibel = (TextView) itemView.findViewById(R.id.maxDecibel);
        }
    }
}
