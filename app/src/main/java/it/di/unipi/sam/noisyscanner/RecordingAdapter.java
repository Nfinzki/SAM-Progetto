package it.di.unipi.sam.noisyscanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

import it.di.unipi.sam.noisyscanner.database.Recording;

public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.RecordViewHolder> {
    List<Recording> recordings;

    public void setRecordings(List<Recording> recordings) {
        this.recordings = recordings;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.last_recording_item, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.city.setText(recordings.get(position).city);
        holder.timestamp.setText(recordings.get(position).timestamp);
        String decibel = recordings.get(position).decibel + " Db";
        holder.decibel.setText(decibel);
    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView city;
        TextView timestamp;
        TextView decibel;

        RecordViewHolder(View itemView) {
            super(itemView);

            card = (MaterialCardView) itemView.findViewById(R.id.recording_item);
            city = (TextView) itemView.findViewById(R.id.city);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
            decibel = (TextView) itemView.findViewById(R.id.decibel);
        }
    }
}
