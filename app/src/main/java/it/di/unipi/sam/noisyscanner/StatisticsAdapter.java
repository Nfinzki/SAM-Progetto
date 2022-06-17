package it.di.unipi.sam.noisyscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticViewHolder> {
    private String loudestHour = null;
    private String loudestDay = null;
    private String loudestMonth = null;
    private String loudestCity = null;

    private final int numStatistics = 4;

    public void setData(String loudestHour, String loudestDay, String loudestMonth, String loudestCity) {
        this.loudestHour = loudestHour;
        this.loudestDay = loudestDay;
        this.loudestMonth = loudestMonth;
        this.loudestCity = loudestCity;
    }

    @NonNull
    @Override
    public StatisticsAdapter.StatisticViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_item, parent, false);
        return new StatisticsAdapter.StatisticViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsAdapter.StatisticViewHolder holder, int position) {
        String title = null;
        String value = null;

        switch (position) {
            case 0:
                title = "Loudest Hour";
                value = loudestHour;
                break;
            case 1:
                title = "Loudest Day";
                value = loudestDay;
                break;
            case 2:
                title = "Loudest Month";
                value = loudestMonth;
                break;
            case 3:
                title = "Loudest City";
                value = loudestCity;
                break;
        }

        holder.title.setText(title);
        holder.value.setText(value);
    }

    @Override
    public int getItemCount() {
        return numStatistics;
    }

    public static class StatisticViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView value;

        StatisticViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            value = (TextView) itemView.findViewById(R.id.value);
        }
    }
}
